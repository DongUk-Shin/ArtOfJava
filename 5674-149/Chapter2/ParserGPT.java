class ParserException2 extends Exception {
    String errStr; // 오류를 설명하는 문자열 
    
    public ParserException2(String str) {
        errStr = str;
    }
    
    public String toString() {
        return errStr;
    }
}

class Parser2 {
    // 이들은 토큰 유형입니다. 
    final int NONE = 0;
    final int DELIMITER = 1;
    final int VARIABLE = 2;
    final int NUMBER = 3;
    final int STRING = 4;
    
    // 이것들은 구문 오류의 유형입니다. 
    final int SYNTAX = 0;
    final int UNBALPARENS = 1;
    final int NOEXP = 2;
    final int DIVBYZERO = 3;
    
    // 이 토큰은 표현식의 끝을 나타냅니다. 
    final String EOE = "\0";
    
    private String exp;   // 표현식 문자열을 참조합니다.  
    private int expIdx;   // 현재 표현식의 인덱스  
    private String token; // 현재 토큰을 보유합니다.  
    private int tokType;  // 토큰의 유형을 보유합니다.  
    
    // 변수 배열입니다.  
    private String varsS[] = new String[26];
    
    
    private void status() { // 현재 상태 출력 테스트용
        System.out.print("현재 토큰: " + token);
        System.out.print(" | 현재 토큰 타입: " + tokType);
        System.out.print(" |  수식: " + exp);
        System.out.println(" | 수식 인덱스: " + expIdx);
    }
    
    public void init() { // 변수 배열을 초기화
        for (int i = 0; i < 26; i++) {  
            varsS[i] = "";
        }
    }
    
    // 구문 분석 시작점입니다.  
    public String evaluate(String expstr) throws ParserException2 {
        
        String result;
        exp = expstr;
        expIdx = 0;
        
        getToken();
        if(token.equals(EOE))
            handleErr(NOEXP); // 표현식이 없음  
        
        // 표현식을 구문 분석하고 평가합니다. 
        result = evalExp1();
        
        if(!token.equals(EOE)) // 마지막 토큰은 EOE 여야 합니다.  맨 마지막 검사
            handleErr(SYNTAX);
        
        return result;
    }
    
    // 할당을 처리합니다.  
    private String evalExp1() throws ParserException2 {
        
        String result;
        int varIdx;
        int ttokType;
        String temptoken;
        
        if(tokType == VARIABLE) { //변수인 경우에만 할당이 이루어짐
            // 이전 토큰 저장  
            temptoken = token;
            ttokType = tokType;
            
            // 변수의 인덱스 계산  
            varIdx = Character.toUpperCase(token.charAt(0)) - 'A';
            
            getToken();
            if(!token.equals("=")) {
                putBack(); // 현재 토큰 반환   인덱스를 조절한다
                // 이전 토큰 복원 - 할당이 아님  
                token = temptoken;
                tokType = ttokType;
            }
            else {
                getToken(); 
                result = evalExp2();
                varsS[varIdx] = result;
                return result;
            }
        }
        return evalExp2();
    }
    
    /*// 두 항을 더하거나 빼줍니다.  
    private String evalExp2() throws ParserException2 {
        
        char op;
        double result;
        String stringResult = ""; // 문자열을 저장할 변수 추가
        double partialResult; //계산 결과
        String partialStringResult = ""; // 부분 문자열을 저장할 변수 추가
        
        if (tokType == STRING) { // 첫 번째 항이 문자열인 경우 문자열 변수에 할당합니다.
            stringResult = token;
            System.out.println("token: "+token);
            while((op = token.charAt(0)) == '+' || op == '-') {
                getToken();
                partialStringResult = token;
                switch(op) {
                    case '-':
                        stringResult = stringResult.replace(partialStringResult, "");
                        break;
                    case '+':
                        stringResult += partialStringResult;
                        break;
                }
            }
            return stringResult;
            
        } else { // 숫자인 경우 double 변수에 할당합니다.
            String temp = evalExp3();
            try {
                result = Double.parseDouble(temp);
            } catch (NumberFormatException e) {
                return temp;
            }
            
            while((op = token.charAt(0)) == '+' || op == '-') {
                getToken();
                partialResult = Double.parseDouble(evalExp3());
                switch(op) {
                    case '-':
                        result = result - partialResult;
                        break;
                    case '+':
                        result = result + partialResult;
                        break;
                }
            }
            return Double.toString(result);
        }
    }*/
    
    private String evalExp2() throws ParserException2 {
        char op;
        String result;
        String partialResult;
        
        result = evalExp3();
        
        while((op = token.charAt(0)) == '+' || op == '-') {
            getToken();
            partialResult = evalExp3();
            switch(op) {
                case '-':
                    if (isNumeric(result) && isNumeric(partialResult)) {
                        // 숫자인 경우 숫자로 처리
                        result = Integer.toString(Integer.parseInt(result) - Integer.parseInt(partialResult));
                    } else {
                        
                    }
                    break;
                case '+':
                    if (isNumeric(result) && isNumeric(partialResult)) {
                        // 숫자인 경우 숫자로 처리
                        result = Double.toString(Double.parseDouble(result) + Double.parseDouble(partialResult));
                    } else {
                        // 문자열인 경우 그대로 두고 연결
                        result += partialResult;
                    }
                    break;
            }
        }
        return result;
    }
    
    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  // 정규식: 숫자인가
    }
    
    // 두 요인을 곱하거나 나눕니다.  
    // 두 요인을 곱하거나 나눕니다.  
    private String evalExp3() throws ParserException2 {
        char op;
        String result;
        String partialResult;
        
        String resultString;
        
        // 첫 번째 항을 계산합니다.
        resultString = evalExp4();
        
        while((op = token.charAt(0)) == '*' || op == '/' || op == '%') {
            getToken();
                // 다음 항이 숫자인 경우에만 연산을 수행합니다.
                partialResult = evalExp4();
                switch (op) {
                    case '*':
                        result *= partialResult;
                        break;
                    case '/':
                        if (partialResult == 0) {
                            throw new ParserException2("0으로 나눌 수 없습니다.");
                        }
                        result /= partialResult;
                        break;
                    case '%':
                        if (partialResult == 0) {
                            throw new ParserException2("0으로 나눌 수 없습니다.");
                        }
                        result %= partialResult;
                        break;
                }
            }
        
        
        /*result = Double.parseDouble(resultString);
        
        // 연산자와 다음 항을 반복하여 평가합니다.
        while((op = token.charAt(0)) == '*' || op == '/' || op == '%') {
            
            getToken();
            if (tokType == NUMBER) {
                // 다음 항이 숫자인 경우에만 연산을 수행합니다.
                partialResult = Double.parseDouble(evalExp4());
                switch (op) {
                    case '*':
                        result *= partialResult;
                        break;
                    case '/':
                        if (partialResult == 0) {
                            throw new ParserException2("0으로 나눌 수 없습니다.");
                        }
                        result /= partialResult;
                        break;
                    case '%':
                        if (partialResult == 0) {
                            throw new ParserException2("0으로 나눌 수 없습니다.");
                        }
                        result %= partialResult;
                        break;
                }
            }
        }*/
        
        return result;
    }
    
    // 지수를 처리합니다.  
    private String evalExp4() throws ParserException2 {
        
        String result;
        String partialResult;
        
        result = evalExp5();
        
        /*if(token.equals("^")) {
            getToken();
            partialResult = evalExp4();
            // 지수 계산은 문자열에 대한 연산이 아니므로 생략합니다.
        }*/
        
        return result;
        
    }
    
    // 단항 + 또는 -를 평가합니다.  
    private String evalExp5() throws ParserException2 {
        
        String result;
        result = evalExp6();
        
        return result;
    }
    
    // 괄호로 둘러싸인 표현식을 처리합니다.  
    private String evalExp6() throws ParserException2 {
        
        String result;
        
        if(token.equals("(")) {
            getToken();
            result = evalExp2();
            if(!token.equals(")"))
                handleErr(UNBALPARENS);
            getToken();
        }
        else result = atom();
        
        return result;
    }
    
    // 숫자 또는 변수의 값을 가져옵니다.  
    private String atom() throws ParserException2 {
        
        String result = "";
        
        switch(tokType) {
            case NUMBER:
                result = token;
                getToken();
                break;
            case VARIABLE:
                result = findVar(token);
                getToken();
                break;
            case STRING:
                result = token;
                getToken();
                break;
            default:
                handleErr(SYNTAX);
                break;
        }
        
        return result;
    }
    
    // 변수의 값을 반환합니다.  
    private String findVar(String vname) throws ParserException2 {
        
        if(!Character.isLetter(vname.charAt(0))){
            handleErr(SYNTAX);
            return "";
        }
        
        int temp = Character.toUpperCase(vname.charAt(0))-'A';
        /*System.out.println("tempp = " + temp);
        System.out.println("varsS[temp] = " + varsS[temp]);*/
        return varsS[temp];
    }
    
    // 토큰을 입력 스트림으로 돌려놓습니다.  
    private void putBack() {
        
        if(token == EOE)
            return;
        
        for(int i=0; i < token.length(); i++)
            expIdx--;
    }
    
    // 오류를 처리합니다.  
    private void handleErr(int error) throws ParserException2 {
        String[] err = {
                "구문 오류",
                "괄호 불균형",
                "표현식 없음",
                "0으로 나눔"
        };
        
        throw new ParserException2(err[error]);
    }
    
    // 다음 토큰을 가져옵니다.  
    private void getToken() {
        tokType = NONE;
        token = "";
        
        // 표현식의 끝인지 확인합니다.  
        if(expIdx == exp.length()) {
            token = EOE;
            return;
        }
        
        // 공백을 건너뜁니다. 
        while(expIdx < exp.length() && Character.isWhitespace(exp.charAt(expIdx))) {
            ++expIdx;
        }
        
        // 마지막 공백은 표현식을 종료합니다. 
        if(expIdx == exp.length()) {
            token = EOE;
            return;
        }
        
        
        if(exp.charAt(expIdx) =='"') { // " 면
            expIdx++; // " 건너뛰고
            
            while (exp.charAt(expIdx) != '"') {
                token += exp.charAt(expIdx);
                expIdx++;
                if(expIdx >= exp.length())
                    break;
            }
            expIdx++;
            
            tokType = STRING;
        }
        
        else if(isDelim(exp.charAt(expIdx))) { // 연산자인지 확인  
            token += exp.charAt(expIdx);
            expIdx++;
            tokType = DELIMITER;
        }
        
        else if(Character.isLetter(exp.charAt(expIdx))) { // 변수인지 확인  
            while(!isDelim(exp.charAt(expIdx))) {
                token += exp.charAt(expIdx);
                expIdx++;
                
                if(expIdx >= exp.length())
                    break;
                
            }
            tokType = VARIABLE;
        }
        
        else if(Character.isDigit(exp.charAt(expIdx))) { // 숫자인지 확인  
            while(!isDelim(exp.charAt(expIdx))) {
                token += exp.charAt(expIdx);
                expIdx++;
                if(expIdx >= exp.length())
                    break;
            }
            tokType = NUMBER;
        }
        else { // 알 수 없는 문자가 표현식을 종료합니다. 
            token = EOE;
            return;
        }
        //status();
    }
    
    // c가 구분자인 경우 true를 반환합니다.  
    private boolean isDelim(char c)
    {
        if((" +-/*%^=()".indexOf(c) != -1))
            return true;
        return false;
    }
}
