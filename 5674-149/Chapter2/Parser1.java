/*   
   이 모듈은 변수를 사용하는 재귀 하향 구문 분석기를 포함하고 있습니다.  
*/

// 구문 분석 오류를 위한 예외 클래스입니다.  
class ParserException1 extends Exception {
    String errStr; // 오류를 설명하는 문자열 
    
    public ParserException1(String str) {
        errStr = str;
    }
    
    public String toString() {
        return errStr;
    }
}


class Parser1 { 
    // 이들은 토큰 유형입니다. 
    final int NONE = 0;
    final int DELIMITER = 1;
    final int VARIABLE = 2;
    final int NUMBER = 3;
    //추가
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
    private double varsD[] = new double[26];
    private String varsS[] = new String[26];
    
    
    private void status() { // 현재 상태 출력 테스트용
        System.out.print("현재 토큰: " + token);
        System.out.print(" | 현재 토큰 타입: " + tokType);
        System.out.print(" |  수식: " + exp);
        System.out.println(" | 수식 인덱스: " + expIdx);
    }
    
    
    // 구문 분석 시작점입니다.  
    public double evaluate(String expstr) throws ParserException2 {
        double result;
        exp = expstr;
        expIdx = 0;
        
        getToken();
        if(token.equals(EOE))
            handleErr(NOEXP); // 표현식이 없음  
        
        // 표현식을 구문 분석하고 평가합니다. 
        result = evalExp1();
        
        /*if(!token.equals(EOE)) // 마지막 토큰은 EOE 여야 합니다.  맨 마지막 검사
            handleErr(SYNTAX);*/
        
        return result;
    }
  
    // 할당을 처리합니다.  
    private double evalExp1() throws ParserException2 {
        double result;
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
                getToken(); // 표현식의 다음 부분 가져오기  
                if (tokType != STRING) { // 추가한 부분 - 토큰 타입 검사해서 문자열인지 아닌지
                    result = evalExp2();
                    varsD[varIdx] = result;
                    return result;
                } else {
                    result = 10000;
                    result = evalExp2();
                    varsS[varIdx] = token;
                    return result;
                } 
            }
        }
        
        return evalExp2();
    }
    
    // 두 항을 더하거나 빼줍니다.  
    private double evalExp2() throws ParserException2 {
        char op;
        double result;
        String stringResult = ""; // 문자열을 저장할 변수 추가
        double partialResult;
        String partialStringResult = ""; // 부분 문자열을 저장할 변수 추가
        
        if (tokType == STRING) { // 첫 번째 항이 문자열인 경우 문자열 변수에 할당합니다.
            stringResult = token;
            
            while((op = token.charAt(0)) == '+' || op == '-') {
                getToken();
                partialStringResult = token;
                
                // 연산자에 따라 연산을 수행합니다.
                switch(op) {
                    case '-':
                        stringResult = stringResult.replace(partialStringResult, "");
                        break;
                    case '+':
                        stringResult += partialStringResult;
                        break;
                }
            }
            return Double.parseDouble(stringResult.toString());
            
        } else { // 숫자인 경우 double 변수에 할당합니다.
            result = evalExp3();
            while((op = token.charAt(0)) == '+' || op == '-') {
                getToken();
                partialResult = evalExp3();
                switch(op) {
                    case '-':
                        result = result - partialResult;
                        break;
                    case '+':
                        result = result + partialResult;
                        break;
                }
            }
            return result;
        }
    }
    
    // 두 요인을 곱하거나 나눕니다.  
    private double evalExp3() throws ParserException2 {
        char op;
        double result;
        double partialResult;
        
        result = evalExp4();
        
        while((op = token.charAt(0)) == '*' ||
                op == '/' || op == '%') {
          getToken();
          partialResult = evalExp4();
          switch(op) {
            case '*':
              result = result * partialResult;
              break;
            case '/':
              if(partialResult == 0.0)
                handleErr(DIVBYZERO);
              result = result / partialResult;
              break;
            case '%':
              if(partialResult == 0.0)
                handleErr(DIVBYZERO);
              result = result % partialResult;
              break;
          }
        }
        return result;
    }
    
    // 지수를 처리합니다.  
    private double evalExp4() throws ParserException2 {
        double result;
        double partialResult;
        double ex;
        int t;
        
        result = evalExp5();
        
        if(token.equals("^")) {
          getToken();
          partialResult = evalExp4();
          ex = result;
          if(partialResult == 0.0) {
            result = 1.0;
          } else
            for(t=(int)partialResult-1; t > 0; t--)
              result = result * ex;
        }
        return result;
    }
    
    // 단항 + 또는 -를 평가합니다.  
    private double evalExp5() throws ParserException2 {
        double result;
        String  op;
        
        op = "";
        if((tokType == DELIMITER) &&
                token.equals("+") || token.equals("-")) {
          op = token;
          getToken();
        }
        result = evalExp6();
        
        if(op.equals("-")) result = -result;
        
        return result;
    }
    
    // 괄호로 둘러싸인 표현식을 처리합니다.  
    private double evalExp6() throws ParserException2 {
        double result;
        
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
    private double atom() throws ParserException2 {
        double result = 0.0;
        
        switch(tokType) {
          case NUMBER:
            try {
              result = Double.parseDouble(token);
            } catch (NumberFormatException exc) {
              handleErr(SYNTAX);
            }
            getToken();
            break;
          case VARIABLE:
            result = findVar(token);
            getToken();
            break;
          default:
            handleErr(SYNTAX);
            break;
        }
        return result;
    }
    
    // 변수의 값을 반환합니다.  
    private double findVar(String vname) throws ParserException2 {
        if(!Character.isLetter(vname.charAt(0))){
          handleErr(SYNTAX);
          return 0.0;
        }
        return varsD[Character.toUpperCase(vname.charAt(0))-'A'];
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
        status();
        
        
    }
    
    // c가 구분자인 경우 true를 반환합니다.  
    private boolean isDelim(char c)
    {
        if((" +-/*%^=()".indexOf(c) != -1))
            return true;
        return false;
    }
}
