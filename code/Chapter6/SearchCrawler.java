package code.Chapter6;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.table.*;

// 웹 크롤러 검색
public class SearchCrawler extends JFrame {
    // 최대 URL 드롭 다운 값.
    private static final String[] MAX_URLS = {"50", "100", "500", "1000"};
    
    // 로봇 제한 목록 캐시.
    private HashMap disallowListCache = new HashMap();
    
    // 검색 GUI 컨트롤.
    private JTextField startTextField;
    private JComboBox maxComboBox;
    private JCheckBox limitCheckBox;
    private JTextField logTextField;
    private JTextField searchTextField;
    private JCheckBox caseCheckBox;
    private JButton searchButton;
    
    // 검색 통계 GUI 컨트롤.
    private JLabel crawlingLabel2;
    private JLabel crawledLabel2;
    private JLabel toCrawlLabel2;
    private JProgressBar progressBar;
    private JLabel matchesLabel2;
    
    // 검색 일치 목록 테이블.
    private JTable table;
    
    // 크롤링이 진행 중인지 여부 플래그.
    private boolean crawling;
    
    // 일치 로그 파일 출력 작가.
    private PrintWriter logFileWriter;
    
    // 검색 웹 크롤러의 생성자.
    public SearchCrawler() {
        // 응용 프로그램 제목 설정.
        setTitle("검색 크롤러");
        
        // 창 크기 설정.
        setSize(600, 600);
        
        // 창 닫기 이벤트 처리.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });
        
        // 파일 메뉴 설정.
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("파일");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExitMenuItem = new JMenuItem("종료", KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        // 검색 패널 설정.
        JPanel searchPanel = new JPanel();
        GridBagConstraints constraints;
        GridBagLayout layout = new GridBagLayout();
        searchPanel.setLayout(layout);
        
        JLabel startLabel = new JLabel("시작 URL:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(startLabel, constraints);
        searchPanel.add(startLabel);
        
        startTextField = new JTextField();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(startTextField, constraints);
        searchPanel.add(startTextField);
        
        JLabel maxLabel = new JLabel("크롤할 최대 URL 수:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(maxLabel, constraints);
        searchPanel.add(maxLabel);
        
        maxComboBox = new JComboBox(MAX_URLS);
        maxComboBox.setEditable(true);
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(maxComboBox, constraints);
        searchPanel.add(maxComboBox);
        
        limitCheckBox = new JCheckBox("시작 URL 사이트로 크롤링 제한");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 10, 0, 0);
        layout.setConstraints(limitCheckBox, constraints);
        searchPanel.add(limitCheckBox);
        
        JLabel blankLabel = new JLabel();
        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(blankLabel, constraints);
        searchPanel.add(blankLabel);
        
        JLabel logLabel = new JLabel("일치 로그 파일:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(logLabel, constraints);
        searchPanel.add(logLabel);
        
        String file = System.getProperty("user.dir") + System.getProperty("file.separator") + "crawler.log";
        logTextField = new JTextField(file);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(logTextField, constraints);
        searchPanel.add(logTextField);
        
        JLabel searchLabel = new JLabel("검색 문자열:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(searchLabel, constraints);
        searchPanel.add(searchLabel);
        
        searchTextField = new JTextField();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 0, 0);
        constraints.gridwidth = 2;
        constraints.weightx = 1.0d;
        layout.setConstraints(searchTextField, constraints);
        searchPanel.add(searchTextField);
        
        caseCheckBox = new JCheckBox("대소문자 구분");
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 0, 5);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(caseCheckBox, constraints);
        searchPanel.add(caseCheckBox);
        
        searchButton = new JButton("검색");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionSearch();
            }
        });
        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        layout.setConstraints(searchButton, constraints);
        searchPanel.add(searchButton);
        
        JSeparator separator = new JSeparator();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        layout.setConstraints(separator, constraints);
        searchPanel.add(separator);
        
        JLabel crawlingLabel1 = new JLabel("크롤링:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(crawlingLabel1, constraints);
        searchPanel.add(crawlingLabel1);
        
        crawlingLabel2 = new JLabel();
        crawlingLabel2.setFont(crawlingLabel2.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(crawlingLabel2, constraints);
        searchPanel.add(crawlingLabel2);
        
        JLabel crawledLabel1 = new JLabel("크롤링된 URL:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(crawledLabel1, constraints);
        searchPanel.add(crawledLabel1);
        
        crawledLabel2 = new JLabel();
        crawledLabel2.setFont(crawledLabel2.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(crawledLabel2, constraints);
        searchPanel.add(crawledLabel2);
        
        JLabel toCrawlLabel1 = new JLabel("크롤링할 URL:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(toCrawlLabel1, constraints);
        searchPanel.add(toCrawlLabel1);
        
        toCrawlLabel2 = new JLabel();
        toCrawlLabel2.setFont(toCrawlLabel2.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(toCrawlLabel2, constraints);
        searchPanel.add(toCrawlLabel2);
        
        JLabel progressLabel = new JLabel("크롤링 진행률:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(progressLabel, constraints);
        searchPanel.add(progressLabel);
        
        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setStringPainted(true);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(progressBar, constraints);
        searchPanel.add(progressBar);
        
        JLabel matchesLabel1 = new JLabel("검색 일치:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 10, 0);
        layout.setConstraints(matchesLabel1, constraints);
        searchPanel.add(matchesLabel1);
        
        matchesLabel2 = new JLabel();
        matchesLabel2.setFont(matchesLabel2.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 10, 5);
        layout.setConstraints(matchesLabel2, constraints);
        searchPanel.add(matchesLabel2);
        
        // 검색 상태 리셋
        table = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"URL"}) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        
        // 일치 패널 설정.
        JPanel matchesPanel = new JPanel();
        matchesPanel.setBorder(BorderFactory.createTitledBorder("일치"));
        matchesPanel.setLayout(new BorderLayout());
        matchesPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // 표시할 패널 추가.
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(searchPanel, BorderLayout.NORTH);
        getContentPane().add(matchesPanel, BorderLayout.CENTER);
    }
    
    // 이 프로그램 종료.
    private void actionExit() {
        System.exit(0);
    }
    
    // 검색/중지 버튼 클릭 처리.
    private void actionSearch() {
        // 중지 버튼이 클릭된 경우, 크롤링 플래그를 끕니다.
        if (crawling) {
            crawling = false;
            return;
        }
        
        // 에러 메세지 저장
        ArrayList errorList = new ArrayList();
        
        // 시작 URL이 입력되었는지 확인합니다.
        String startUrl = startTextField.getText().trim();
        if (startUrl.length() < 1) {
            errorList.add("시작 URL이 누락되었습니다.");
        }
        // 시작 URL 확인.
        else if (verifyUrl(startUrl) == null) {
            errorList.add("잘못된 시작 URL입니다.");
        }

        // 최대 URL이 비어 있거나 숫자인지 확인합니다.
        int maxUrls = 0;
        String max = ((String) maxComboBox.getSelectedItem()).trim();
        if (max.length() > 0) {
            try {
                maxUrls = Integer.parseInt(max);
            } catch (NumberFormatException e) {
            }
            if (maxUrls < 1) {
                errorList.add("잘못된 최대 URL 값입니다.");
            }
        }

        // 일치 로그 파일이 입력되었는지 확인합니다.
        String logFile = logTextField.getText().trim();
        if (logFile.length() < 1) {
            errorList.add("일치 로그 파일이 누락되었습니다.");
        }

        // 검색 문자열이 입력되었는지 확인합니다.
        String searchString = searchTextField.getText().trim();
        if (searchString.length() < 1) {
            errorList.add("검색 문자열이 누락되었습니다.");
        }
        
        // 에러가 있으면 표시하고 반환합니다.
        if (errorList.size() > 0) {
            StringBuffer message = new StringBuffer();
            
            // 에러를 하나의 메시지로 연결합니다.
            for (int i = 0; i < errorList.size(); i++) {
                message.append(errorList.get(i));
                if (i + 1 < errorList.size()) {
                    message.append("\n");
                }
            }
            
            showError(message.toString());
            return;
        }
        
        // 시작 URL에서 "www"를 제거합니다.
        startUrl = removeWwwFromUrl(startUrl);
        
        // 검색 크롤러 시작.
        search(logFile, startUrl, maxUrls, searchString);
    }
    
    private void search(final String logFile, final String startUrl, final int maxUrls, final String searchString) {
        // 새로운 스레드에서 검색 시작.
        Thread thread = new Thread(new Runnable() {
            public void run() {
                // 크롤링 중임을 나타내는 시계 모양 커서 표시.
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                // 검색 컨트롤 비활성화.
                startTextField.setEnabled(false);
                maxComboBox.setEnabled(false);
                limitCheckBox.setEnabled(false);
                logTextField.setEnabled(false);
                searchTextField.setEnabled(false);
                caseCheckBox.setEnabled(false);
                
                // 검색 버튼을 "Stop"으로 전환.
                searchButton.setText("Stop");
                
                // 통계 초기화.
                table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"URL"}) {
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
                updateStats(startUrl, 0, 0, maxUrls);
                
                // 일치 로그 파일 열기.
                try {
                    logFileWriter = new PrintWriter(new FileWriter(logFile));
                } catch (Exception e) {
                    showError("일치 로그 파일을 열 수 없습니다.");
                    return;
                }
                
                // 크롤링 플래그 켜기.
                crawling = true;

                // 실제 크롤링 수행.
                crawl(startUrl, maxUrls, limitCheckBox.isSelected(), searchString, caseCheckBox.isSelected());
                
                // 크롤링 플래그 끄기.
                crawling = false;
                
                // 일치 로그 파일 닫기.
                try {
                    logFileWriter.close();
                } catch (Exception e) {
                    showError("일치 로그 파일을 닫을 수 없습니다.");
                }

                // 검색 완료로 표시.
                crawlingLabel2.setText("완료");

                // 검색 컨트롤 활성화.
                startTextField.setEnabled(true);
                maxComboBox.setEnabled(true);
                limitCheckBox.setEnabled(true);
                logTextField.setEnabled(true);
                searchTextField.setEnabled(true);
                caseCheckBox.setEnabled(true);
                
                // 검색 버튼을 다시 "Search"로 전환.
                searchButton.setText("검색");

                // 기본 커서로 돌아가기.
                setCursor(Cursor.getDefaultCursor());

                // 검색 문자열을 찾을 수 없는 경우 메시지 표시.
                if (table.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(SearchCrawler.this, "검색 문자열을 찾을 수 없습니다. 다른 문자열을 시도하세요.", "검색 문자열을 찾을 수 없음", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        thread.start();
    }
    
    // 오류 메시지가 있는 대화 상자 표시.
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
    }
    
    // 검색 상태 갱신
    private void updateStats(String crawling, int crawled, int toCrawl, int maxUrls) {
        crawlingLabel2.setText(crawling);
        crawledLabel2.setText("" + crawled);
        toCrawlLabel2.setText("" + toCrawl);
        
        // 프로그레스 바 업데이트.
        if (maxUrls == -1) {
            progressBar.setMaximum(crawled + toCrawl);
        } else {
            progressBar.setMaximum(maxUrls);
        }
        progressBar.setValue(crawled);
        
        matchesLabel2.setText("" + table.getRowCount());
    }
    
    // 일치를 일치 테이블과 로그 파일에 추가합니다.
    private void addMatch(String url) {
        // URL을 일치 테이블에 추가합니다.
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{url});
        
        // URL을 일치 로그 파일에 추가합니다.
        try {
            logFileWriter.println(url);
        } catch (Exception e) {
            showError("일치를 로그할 수 없습니다.");
        }
    }
    
    // URL 형식 확인.
    private URL verifyUrl(String url) {
        // HTTP URL 만 허용합니다.
        if (!url.toLowerCase().startsWith("http://")) 
            return null;
        
        // URL 형식 확인.
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }
        
        return verifiedUrl;
    }
    
    //로봇이 주어진 URL에 액세스할 수 있는지 확인합니다.
    private boolean isRobotAllowed(URL urlToCheck) {
        String host = urlToCheck.getHost().toLowerCase();
        
        // 캐시에서 호스트의 로봇 금지 목록을 검색합니다.
        ArrayList disallowList = (ArrayList) disallowListCache.get(host);
        
        // 목록이 캐시에 없으면 다운로드하여 캐시에 저장합니다.
        if (disallowList == null) {
            disallowList = new ArrayList();
            
            try {
                URL robotsFileUrl = new URL("http://" + host + "/robots.txt");
                
                // 로봇 파일 URL에 대한 연결 열기 (읽기 용).
                BufferedReader reader = new BufferedReader(new InputStreamReader(robotsFileUrl.openStream()));
                
                // 로봇 파일 읽기, 허용되지 않는 경로의 목록 만들기.
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.indexOf("Disallow:") == 0) {
                        String disallowPath = line.substring("Disallow:".length());
                        
                        // 주석이 있는지 확인하고 있는 경우 제거합니다.
                        int commentIndex = disallowPath.indexOf("#");
                        if (commentIndex != -1) {
                            disallowPath = disallowPath.substring(0, commentIndex);
                        }
                        
                        // 허용되지 않는 경로의 앞뒤 공백 제거.
                        disallowPath = disallowPath.trim();
                        
                        // 허용되지 않는 경로를 목록에 추가합니다.
                        disallowList.add(disallowPath);
                    }
                }
                
                // 새로운 허용되지 않는 목록을 캐시에 추가합니다.
                disallowListCache.put(host, disallowList);
            } catch (Exception e) {
                /* 로봇 파일이 존재하지 않는 경우 예외가 발생하므로
                예외가 발생하면 로봇이 허용된 것으로 가정합니다. */
                return true;
            }
        }
        
        /* 주어진 URL에 대해 크롤링이 허용되는지 확인하기 위해
        허용되지 않는 목록을 반복합니다. */
        String file = urlToCheck.getFile();
        for (int i = 0; i < disallowList.size(); i++) {
            String disallow = (String) disallowList.get(i);
            if (file.startsWith(disallow)) {
                return false;
            }
        }
        
        return true;
    }
    
    // 주어진 URL에서 페이지 다운로드하기.
    private String downloadPage(URL pageUrl) {
        try {
            // 읽기용 URL에 대한 연결 열기.
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageUrl.openStream()));
            
            // 페이지를 버퍼에 읽기.
            String line;
            StringBuffer pageBuffer = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                pageBuffer.append(line);
            }
            
            return pageBuffer.toString();
        } catch (Exception e) {
        }
        
        return null;
    }
    
    // URL에서 "www"를 제거합니다.
    private String removeWwwFromUrl(String url) {
        int index = url.indexOf("://www.");
        if (index != -1) {
            return url.substring(0, index + 3) + url.substring(index + 7);
        }
        
        return (url);
    }
    
    // 페이지 내용을 파싱하고 링크를 검색합니다.
    private ArrayList retrieveLinks(URL pageUrl, String pageContents, HashSet crawledList, boolean limitHost) {
        // 링크 매칭 패턴 컴파일.
        Pattern p = Pattern.compile("<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(pageContents);
        
        // 링크 매칭 목록 생성.
        ArrayList linkList = new ArrayList();
        while (m.find()) {
            String link = m.group(1).trim();
            
            // 빈 링크 건너뛰기.
            if (link.length() < 1) {
                continue;
            }
            
            // 페이지 앵커인 링크 건너뛰기.
            if (link.charAt(0) == '#') {
                continue;
            }
            
            // mailto 링크 건너뛰기.
            if (link.indexOf("mailto:") != -1) {
                continue;
            }
            
            // JavaScript 링크 건너뛰기.
            if (link.toLowerCase().indexOf("javascript") != -1) {
                continue;
            }
            
            // 필요에 따라 절대 및 상대 URL에 접두사 추가.
            if (link.indexOf("://") == -1) {
                // 절대 URL 처리.
                if (link.charAt(0) == '/') {
                    link = "http://" + pageUrl.getHost() + link;
                    // 상대 URL 처리.
                } else {
                    String file = pageUrl.getFile();
                    if (file.indexOf('/') == -1) {
                        link = "http://" + pageUrl.getHost() + "/" + link;
                    } else {
                        String path = file.substring(0, file.lastIndexOf('/') + 1);
                        link = "http://" + pageUrl.getHost() + path + link;
                    }
                }
            }
            
            // 링크에서 앵커 제거.
            int index = link.indexOf('#');
            if (index != -1) {
                link = link.substring(0, index);
            }
            
            // URL의 호스트에서 "www" 제거.
            link = removeWwwFromUrl(link);
            
            // 확인된 링크가 유효한지 확인하고 유효하지 않은 경우 건너뜁니다.
            URL verifiedLink = verifyUrl(link);
            if (verifiedLink == null) {
                continue;
            }
            
            /* 지정된 경우, 시작 URL과 동일한 호스트를
             가진 링크에만 제한합니다. */
            if (limitHost && !pageUrl.getHost().toLowerCase().equals(verifiedLink.getHost().toLowerCase())) {
                continue;
            }
            
            // 이미 크롤링된 링크인 경우 건너뜁니다.
            if (crawledList.contains(link)) {
                continue;
            }
            
            // 링크를 목록에 추가합니다.
            linkList.add(link);
        }
        
        return (linkList);
    }
    
    /* 주어진 페이지 내용에서 검색 문자열이
       일치하는지 여부를 결정합니다. */
    private boolean searchStringMatches(String pageContents, String searchString, boolean caseSensitive) {
        String searchContents = pageContents;

    /* 대소문자 구분 검색인 경우 페이지 내용을
       비교를 위해 소문자로 변환합니다. */
        if (!caseSensitive) {
            searchContents = pageContents.toLowerCase();
        }
        
        // 검색 문자열을 개별 용어로 분할합니다.
        Pattern p = Pattern.compile("[\\s]+");
        String[] terms = p.split(searchString);
        
        // 각 용어가 일치하는지 확인합니다.
        for (int i = 0; i < terms.length; i++) {
            if (caseSensitive) {
                if (searchContents.indexOf(terms[i]) == -1) {
                    return false;
                }
            } else {
                if (searchContents.indexOf(terms[i].toLowerCase()) == -1) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    // 실제 크롤링을 수행하고 검색 문자열을 찾습니다.
    public void crawl(String startUrl, int maxUrls, boolean limitHost, String searchString, boolean caseSensitive) {
        // 크롤링에 사용할 목록 설정.
        HashSet crawledList = new HashSet();
        LinkedHashSet toCrawlList = new LinkedHashSet();
        
        // 시작 URL을 크롤링할 목록에 추가합니다.
        toCrawlList.add(startUrl);

        /* 지정된 경우 실제 크롤링을 수행하여
           크롤링할 목록을 반복합니다. */
        while (crawling && toCrawlList.size() > 0) {
          /* 지정된 경우 최대 URL 수가
             지정되었는지 확인합니다. */
            if (maxUrls != -1) {
                if (crawledList.size() == maxUrls) {
                    break;
                }
            }
            
            // 목록 아래의 URL 가져오기.
            String url = (String) toCrawlList.iterator().next();
            
            // 크롤링할 목록에서 URL 제거.
            toCrawlList.remove(url);
            
            // 문자열 URL을 URL 개체로 변환합니다.
            URL verifiedUrl = verifyUrl(url);
            
            // 로봇이 액세스할 수 없는 경우 URL 건너뛰기.
            if (!isRobotAllowed(verifiedUrl)) {
                continue;
            }
            
            // 크롤링 통계 업데이트.
            updateStats(url, crawledList.size(), toCrawlList.size(), maxUrls);
            
            // 크롤링된 목록에 페이지 추가.
            crawledList.add(url);
            
            // 주어진 URL에서 페이지 다운로드.
            String pageContents = downloadPage(verifiedUrl);

           /* 페이지 다운로드가 성공하면 모든 링크를 검색하고
             검색 문자열이 포함되어 있는지 확인합니다. */
            if (pageContents != null && pageContents.length() > 0) {
                // 페이지에서 유효한 링크 목록 검색.
                ArrayList links = retrieveLinks(verifiedUrl, pageContents, crawledList, limitHost);
                
                // 링크를 크롤링할 목록에 추가합니다.
                toCrawlList.addAll(links);

                /* 페이지에 검색 문자열이 포함되어 있는지
                   확인하고 일치하는 경우 기록합니다. */
                if (searchStringMatches(pageContents, searchString, caseSensitive)) {
                    addMatch(url);
                }
            }
            
            // 크롤링 통계 업데이트.
            updateStats(url, crawledList.size(), toCrawlList.size(), maxUrls);
        }
    }
    
    // Search Crawler를 실행합니다.
    public static void main(String[] args) {
        SearchCrawler crawler = new SearchCrawler();
        crawler.show();
    }
}
