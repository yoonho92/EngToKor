package EngToKor;


import java.util.*;

public class EngToKor {
    public static void main(String[] args) {
        EngToKorAct("rk sk");

    }

    private static void EngToKorAct(String input) {
        String[] choEng = {"r", "R", "s", "e", "E", "f", "a", "q", "Q", "t", "T", "d", "w", "W", "c", "z", "x", "v", "g"};
        String[] joongEng = {"k", "o", "i", "O", "j", "p", "u", "P", "h", "hk", "ho", "hl", "y", "n", "nj", "np", "nl", "b", "m", "ml", "l"};
        String[] jongEng = {"", "r", "R", "rt", "s", "sw", "sg", "e", "f", "fr", "fa", "fq", "ft", "fx", "fv", "fg", "a", "q", "qt", "t", "T", "d", "w", "c", "z", "x", "v", "g"};


        int choNum = -1;
        int joongNum = -1;
        int jongNum = -1;
        int min = 0;
        Boolean check = false;
        boolean space = false;
        ArrayList<Integer> Temp = new ArrayList<>();
        String chr;
        StringBuilder SB = new StringBuilder();
        outerloop:
        for (int i = 0; i < input.length(); i++) { //영문자에 해당하는 초성찾기
            if (input.substring(i, i + 1).equals(" ")) { // 현재 i의 위치가 공백일 경우 for문의 마지막에서 띄어쓰기 추가
                check = true;
            }
            if (choNum == -1) { //초성이 찾아진 상태인지 여부를 확인함, 한글 한글자가 완성되면 -1로 초기화
                chr = input.substring(i, i + 1); // input 변수에서 i번째에 해당하는 첫번째 문자 추출
                for (int j = 0; j < choEng.length; j++) {
                    if (chr.equals(choEng[j])) { // 일치하는 초성 비교
                        choNum = j;
                        continue outerloop; //일치하는 초성 찾을시 외부 루프 continue
                    }
                }
            }
            if (joongNum == -1) { //중성이 찾아진 상태인지 여부를 확인함, 한글 한글자가 완성되면 -1로 초기화

                if (i < input.length() - 1) {
                    chr = input.substring(i, i + 2); // ㅘ ㅢ 같은 중성을 먼저 검출하기위해 input의 i번째의 두글자를 추출
                    for (int j = 0; j < joongEng.length; j++) {
                        if (chr.equals(joongEng[j])) { // 일치하는 중성 비교
                            joongNum = j;
                            i += 1; //두글자를 추출하여 찾은 경우이므로 다음 for문에서 중복되는 글자를 찾지않도록 i에 1더하
                            continue outerloop; // 일치하는 중성 찾을시 외부 루프 continue
                        }
                    }
                }
                chr = input.substring(i, i + 1); // nput 변수에서 i번째에 해당하는 첫번째 문자 추출
                for (int j = 0; j < joongEng.length; j++) {
                    if (chr.equals(joongEng[j])) { // 일치하는 중성 비교
                        joongNum = j;
                        if (i != input.length() - 1) continue outerloop; // 일치하는 중성 찾을시 외부 루프 continue
                    }
                }
            }
            if ((choNum != -1) && joongNum != -1) { // 초성과 중성이 찾아진 상태이어야 종성이 들어갈수 있기 때문에 여부 체크
                for (String joongChr : joongEng) {
                    Temp.add(input.indexOf(joongChr, i)); //현재 i번째에 해당하는 문자열을 기준으로 다음에 있는 중성의 인덱스 추가
                }
                Temp.removeIf(n -> n == -1); // input내에 해당하는 중성이 없을 경우 indexof는 -1을 반환하기때문에 최소값을 찾기위해 -1 제거
                if (!Temp.isEmpty()) { // input 문자열의 마지막에 찾을수 있는 중성이 없을 경우 에러가 나기 때문에 찾아진 중성이 있는지를 체크
                    Collections.sort(Temp);
                    min = Temp.get(0);
                    Temp.clear();
                } else {
                    min = input.length(); // 문자열의 마지막에서 종성이 없는 경우를 기본으로 두고 현재 i번째의 문자가 종성에 해당할경우 min에 1을 더해주기
                    for (String endChr : jongEng) {
                        if (input.substring(input.length() - 1).equals(endChr)) {
                            min = input.length() + 1;
                        }
                    }
                }
                int result = min - i;

                if (result == 3 && input.substring(i + 1, i + 2).equals(" ")) { // 중성 위치를 체크할때 공백까지 포함되는 경우가 있으므로 자음의 길이가 3이면서 다음 문자열이 공백인경우 값 보정
                    result -= 1;
                } else if (input.substring(i, i + 1).equals(" ")) result = 0; // 현재 i의 위치가 공백일때 앞 글자가 종성이 없는 글자일경우 보정
                switch (result) { //현재 i번째 문자를 기준으로 다음 중성까지의 크기
                    case 0: // 문자열의 마지막 문자가 중성으로 끝날경우 min값이 마지막 i 값과 같아지므로 종성이 없는 case 1과 똑같이 다루기 위함
                    case 1: // 다음 중성까지 자음인 문자가 1개라는 뜻으로 음이라는 의미로 다음 for문에서 쓰일 초성에 해당
                        SB.append((char) ((choNum * 21 + joongNum) * 28 + 0xAC00)); //초성, 중성, 종성값에 해당하는 유니코드로 변환하기 위한 계산
                        choNum = -1; // 다음 for문을 위해 초기화
                        joongNum = -1;
                        jongNum = -1;
                        if (i != input.length() - 1 && !input.substring(i, i + 1).equals(" "))
                            i -= 1; // 종성이 공백일경우 현재 i는 다음 초성이라는 의미로 초성,중성,종성값의 초기화후 현재의 i값으로 다시 for문을 진행할 수 있도록 i에 1빼주기, if는 문자열의 마지막에서 무한루프를 방지하기 위함
                        break;
                    case 2:
                        chr = input.substring(i, i + 1); // 다음 중성까지 자음인 문자가 2개라는 뜻으로 종성과 다음 for문에서 쓰일 초성에 해당
                        for (int j = 0; j < jongEng.length; j++) { // 현재 i에 해당하는 문자열의 문자를 종성배열에서 찾기
                            if (chr.equals(jongEng[j])) {
                                jongNum = j;
                                break;
                            }
                        }
                        SB.append((char) ((choNum * 21 + joongNum) * 28 + jongNum + 0xAC00));
                        choNum = -1;
                        joongNum = -1;
                        jongNum = -1;
                        break;
                    case 3:
                        chr = input.substring(i, i + 2); // 다음 중성까지 자음인 문자가 3개라는 뜻으로 종성은 받침이 2개인 문자와 다음 for문에서 쓰일 초성에 해당
                        for (int j = 0; j < jongEng.length; j++) { // 현재 i에 해당하는 문자열의 문자를 종성배열에서 찾기
                            if (chr.equals(jongEng[j])) {
                                jongNum = j;
                                break;
                            }
                        }
                        SB.append((char) ((choNum * 21 + joongNum) * 28 + jongNum + 0xAC00));
                        choNum = -1;
                        joongNum = -1;
                        jongNum = -1;
                        i += 1;
                        break;
                }
            }
            if (check){ // 햔재 i의 위치가 공백일때 띄어쓰기 추가
                SB.append(" ");
                check=false;
            }
        }
        System.out.println(SB);
    }


}









