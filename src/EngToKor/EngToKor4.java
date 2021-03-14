package EngToKor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class EngToKor4 {
    //(choNum * 21 + joongNum) * 28 + jongNum + 0xAC00)
    //가능하면 현재 이후의 인덱스를 참조하려하지 말것 변수가 많아짐
    //첫가끝 초성 1100 중성 1161 종성 11A8
    //호환형한글자모 자음 0x3131 모음 0x314F

    public static void main(String[] args) {
        String input = "rrkswk";
        EngToKor(input);
    }

    private static void EngToKor(String input) {
        String[] choEng = {"r", "R", "s", "e", "E", "f", "a", "q", "Q", "t", "T", "d", "w", "W", "c", "z", "x", "v", "g"};
        String[] joongEng = {"k", "o", "i", "O", "j", "p", "u", "P", "h", "hk", "ho", "hl", "y", "n", "nj", "np", "nl", "b", "m", "ml", "l"};
        String[] jongEng = {"", "r", "R", "rt", "s", "sw", "sg", "e", "f", "fr", "fa", "fq", "ft", "fx", "fv", "fg", "a", "q", "qt", "t", "T", "d", "w", "c", "z", "x", "v", "g"};
        StringBuilder state = new StringBuilder();
        StringBuilder output = new StringBuilder();
        Queue<Integer> queue = new LinkedList<>();
        boolean check = false;
        String nowChr = "";
        int choNum = 0;
        int joongNum1 = 0;
        int joongNum2 = 0;
        int joongNum = 0;
        int jongNum1 = 0;
        int jongNum2 = 0;
        int jongNum = 0;

        String beforeTemp;
        String[] upperElement = {"q","w","e","r","t"}; // 통과 시킬 초성문자열
        for (int n = 0; n < input.length(); n++) {
            nowChr = input.substring(n, n + 1);
            String finalNowChr = nowChr;
            if (n > 0 &&  Arrays.stream(upperElement).anyMatch(m -> m.equals(finalNowChr))) { //n이 0이상이면서 upperElement의 요소와 일치하는지 확인
                beforeTemp = input.substring(n - 1, n);
                if (beforeTemp.equals(nowChr)) {  //연속된 문자열이 나온다면 대체
                    input = input.replace(nowChr + nowChr, nowChr.toUpperCase());
                }
            }
        }
        for (int i = 0; i <= input.length(); i++) {

            if (i != input.length()) {
                nowChr = input.substring(i, i + 1);
                if (!((nowChr.codePointAt(0) >= 65 && nowChr.codePointAt(0) <= 90)
                        || (nowChr.codePointAt(0) >= 97 && nowChr.codePointAt(0) <= 122)))
                    check = true; //영문자 이외의 문자가 올 때
                for (int choI = 0; choI < choEng.length; choI++) { //초성찾아서 큐에 넣고 state에 J추가
                    if (choEng[choI].equals(nowChr)) {
                        queue.add(choI);
                        state.append("J");
                        break;
                    }
                }
                for (int joongI = 0; joongI < joongEng.length; joongI++) { //중성찾으면 큐에 넣고 state에 M추가
                    if (joongEng[joongI].equals(nowChr)) {
                        queue.add(joongI);
                        state.append("M");
                        break;
                    }
                }
            }
            //        J
            //        M
            //        J M
            //        J J
            //        M J
            //        J M M
            //        J M J
            //        J M J M
            //        J M J J (M
            //        J M J J (J
            //        J M M J (M
            //        J M M J J (M

            switch (state.toString()) { // state에 추가된 문자열에 따라 동작
                case "J": //특수문자나 인덱스 i가 마지막일 때 문자를 완성
                    if (check || i == input.length()) {
                        choNum = queue.poll();
                        output.append((char) (choNum + 0x1100));
                        state.delete(0, 1);
                    }
                    break;
                case "M": //모음만 단독으로 나올 때 문자를 완성
                    joongNum1 = queue.poll();
                    output.append((char) (joongNum1 + 0x1161));
                    state.delete(0, 1);
                    break;
                case "JM": // 특수문자나 인덱스 i가 마지막일 때 문자를 완성
                    if (check || i == input.length()) {
                        choNum = queue.poll();
                        joongNum1 = queue.poll();
                        state.delete(0, 2);
                        output.append((char) ((choNum * 21 + joongNum1) * 28 + 0xAC00));
                    }
                    break;
                case "JMM": // 특수문자나 인덱스 i가 마지막일 때 문자를 완성하거나 JM과 M으로 문자가 완성되는 경우를 가정
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    joongNum2 = queue.poll();
                    if (check || i == input.length()) {
                        for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                            if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                                joongNum = joongCheck;
                                queue.clear();
                                state.delete(0, 3);
                                output.append((char) ((choNum * 21 + joongNum) * 28 + 0xAC00));
                                break;
                            }
                        }
                    }
                    queue.add(choNum);
                    queue.add(joongNum1);
                    queue.add(joongNum2);
                    int finalJoongNum = joongNum1;
                    int finalJoongNum1 = joongNum2;
                    //MM이 중성 배열에 존재하지 않아 JM과 M으로 각 글자가 완성되는 경우, 중성배열에 존재할 경우 다음에 오는 문자에 따라 JMM JMMJ JMMJJ 로 문자가 완성되므로 다음 인덱스의 switch에 맡기기
                    if (Arrays.stream(joongEng).noneMatch(n -> n.equals(joongEng[finalJoongNum] + joongEng[finalJoongNum1]))) { //MM이 중성모음에 없으면 JM출력
                        output.append((char) ((choNum * 21 + joongNum1) * 28 + 0xAC00));
                        queue.poll();
                        queue.poll();
                        joongNum = queue.poll();
                        output.append((char) (joongNum + 0x1161));
                        state.delete(0, 3);
                    }
                    break;

                case "JJ": // 마지막에 자음이 왔으므로 직전에 왔던 J는 문자를 완성
                    choNum = queue.poll();
                    output.append((char) (choNum + 0x1100));
                    state.delete(0, 1);
                    break;

                case "JMJ": // 특수문자나 인덱스 i가 마지막일 때 문자를 완성하거나 JM로 문자가 완성되는 경우를 가정
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    jongNum1 = queue.poll();
                    queue.add(choNum);
                    queue.add(joongNum1);
                    queue.add(jongNum1);
                    if (check || i == input.length()) {
                        state.delete(0, 3);
                        for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                            if (jongEng[jongCheck].equals(choEng[jongNum1])) {
                                state.delete(0, 3);
                                queue.clear();
                                output.append((char) ((choNum * 21 + joongNum1) * 28 + jongCheck + 0xAC00));
                                break;
                            }
                        }
                    }
                    int finalJongNum4 = jongNum1;
                    //J가 종성 배열에 존재하지 않아 JM로 글자가 완성되는 경우, 종성배열에 존재할 경우 다음에 오는 문자에 따라 JM JMJ JMJJ 로 문자가 완성되므로 다음 인덱스의 switch에 맡기기
                    if (Arrays.stream(jongEng).noneMatch(n -> n.equals(choEng[finalJongNum4]))) {
                        queue.poll();
                        queue.poll();
                        state.delete(0, 2);
                        output.append((char) ((choNum * 21 + joongNum1) * 28 + 0xAC00));
                        break;
                    }

                    break;
                case "JMJM": // 마지막에 모음이 왔으므로 직전에 왔던 J는 종성으로 쓰일 수 없기 때문에 JM으로 문자를 완성
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    output.append((char) ((choNum * 21 + joongNum1) * 28 + 0xAC00));
                    state.delete(0, 2);
                    break;

                case "JMJJ": // 특수문자나 인덱스 i가 마지막일 때 문자를 완성하거나 JMJ로 문자가 완성되는 경우를 가정
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    jongNum1 = queue.poll();
                    jongNum2 = queue.poll();

                    if (check || i == input.length()) {
                        for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                            if (jongEng[jongCheck].equals(choEng[jongNum1] + choEng[jongNum2])) {
                                jongNum =jongCheck;
                                output.append((char) ((choNum * 21 + joongNum1) * 28 + jongNum+ 0xAC00));
                                state.delete(0, 4);
                                break;
                            }
                        }
                    }

                    queue.add(choNum);
                    queue.add(joongNum1);
                    queue.add(jongNum1);
                    queue.add(jongNum2);
                    int finalJongNum = jongNum1;
                    int finalJongNum1 = jongNum2;
                    //JJ가 종성 배열에 존재하지 않아 JMJ로 글자가 완성되는 경우, 종성배열에 존재할 경우 다음에 오는 문자에 따라 JMJ JMJJ로 문자가 완성되므로 다음 인덱스의 switch에 맡기도록 하기
                    if (Arrays.stream(jongEng).noneMatch(n -> n.equals(choEng[finalJongNum] + choEng[finalJongNum1]))) {

                        for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                            if (jongEng[jongCheck].equals(choEng[jongNum1])) {
                                jongNum = jongCheck;
                                queue.poll();
                                queue.poll();
                                queue.poll();
                                state.delete(0, 3);
                                output.append((char) ((choNum * 21 + joongNum1) * 28 + jongNum + 0xAC00));
                                break;
                            }
                        }

                    }
                    break;

                case "JMJJJ": // 마지막에 자음이 왔으므로 직전에 왔던 J는 종성으로 쓰이므로 때문에 JMJJ으로 문자를 완성
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    jongNum1 = queue.poll();
                    jongNum2 = queue.poll();
                    state.delete(0, 4);
                    for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                        if (jongEng[jongCheck].equals(choEng[jongNum1] + choEng[jongNum2])) {
                            jongNum = jongCheck;
                            output.append((char) ((choNum * 21 + joongNum1) * 28 + jongCheck + 0xAC00));
                        }
                    }
                    break;

                case "JMJJM": // 마지막에 모음이 왔으므로 직전에 왔던 J는 종성으로 쓰일 수 없기 때문에 JMJ으로 문자를 완성
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    jongNum1 = queue.poll();
                    state.delete(0, 3);
                    for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                        if (jongEng[jongCheck].equals(choEng[jongNum1])) {
                            output.append((char) ((choNum * 21 + joongNum1) * 28 + jongCheck + 0xAC00));
                            break;
                        }
                    }
                    break;
                case "JMMJ": // 특수문자
                    // 특수문자나 인덱스 i가 마지막일 때 문자를 완성
                    if (check || i == input.length()) {
                        choNum = queue.poll();
                        joongNum1 = queue.poll();
                        joongNum2 = queue.poll();
                        jongNum1 = queue.poll();
                        for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                            if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                                joongNum = joongCheck;
                            }
                        }
                        for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++){
                            if (jongEng[jongCheck].equals(choEng[jongNum1])) {
                                state.delete(0, 4);
                                output.append((char) ((choNum * 21 + joongNum) * 28 + jongCheck + 0xAC00));
                            }

                        }
                    }
                    break;

                case "JMMJM": // 마지막에 모음이 왔으므로 직전에 왔던 J는 종성으로 쓰일 수 없기 때문에 JMM으로 문자를 완성
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    joongNum2 = queue.poll();
                    for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                        if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                            joongNum = joongCheck;
                            state.delete(0, 3);
                            output.append((char) ((choNum * 21 + joongNum) * 28 + 0xAC00));
                        }
                    }
                    break;

                case "JMMJJ": // 특수문자나 인덱스 i가 마지막일 때 문자를 완성하거나 JMMJ로 문자가 완성되는 경우를 가정
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    joongNum2 = queue.poll();
                    jongNum1 = queue.poll();
                    jongNum2 = queue.poll();

                    if (check || i == input.length()) {
                        for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                            if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                                joongNum = joongCheck;
                            }
                        }
                        for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                            if (jongEng[jongCheck].equals(choEng[jongNum1] + choEng[jongNum2])) {
                                jongNum = jongCheck;
                                state.delete(0, 5);
                                output.append((char) ((choNum * 21 + joongNum) * 28 + jongNum + 0xAC00));
                                break;
                            }
                        }
                    }

                    queue.add(choNum);
                    queue.add(joongNum1);
                    queue.add(joongNum2);
                    queue.add(jongNum1);
                    queue.add(jongNum2);
                    int finalJongNum2 = jongNum1;
                    int finalJongNum3 = jongNum2;
                    //JJ가 종성 배열에 존재하지 않아 JMMJ로 글자가 완성되는 경우, 종성배열에 존재할 경우 다음에 오는 문자에 따라 JMMJ JMMJJ로 문자가 완성되므로 다음 인덱스의 switch에 맡기도록 하기
                    if (Arrays.stream(jongEng).noneMatch(n -> n.equals(choEng[finalJongNum2] + choEng[finalJongNum3]))) {
                        for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                            if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                                joongNum = joongCheck;
                            }
                        }
                        for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                            if (jongEng[jongCheck].equals(choEng[jongNum1])) {
                                queue.poll();
                                queue.poll();
                                queue.poll();
                                queue.poll();
                                state.delete(0, 4);
                                output.append((char) ((choNum * 21 + joongNum) * 28 + jongCheck + 0xAC00));
                                break;
                            }
                        }
                    }
                    break;

                case "JMMJJJ": //마지막에 J가 오므로 JMMJJ로 문자 출력
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    joongNum2 = queue.poll();
                    jongNum1 = queue.poll();
                    jongNum2 = queue.poll();
                    state.delete(0, 5);
                    for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                        if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                            joongNum = joongCheck;
                            break;
                        }
                    }
                    for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                        if (jongEng[jongCheck].equals(choEng[jongNum1] + choEng[jongNum2])) {
                            jongNum = jongCheck;
                            output.append((char) ((choNum * 21 + joongNum) * 28 + jongNum + 0xAC00));
                            break;
                        }
                    }
                    break;

                case "JMMJJM": //마지막에 M이 오므로 JMMJ로 문자 출력
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    joongNum2 = queue.poll();
                    jongNum1 = queue.poll();
                    state.delete(0, 4);

                    for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                        if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])){
                            joongNum = joongCheck;
                            break;
                        }
                    }
                    for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                        if (jongEng[jongCheck].equals(choEng[jongNum1])) {
                            jongNum = jongCheck;
                            output.append((char) ((choNum * 21 + joongNum) * 28 + jongCheck + 0xAC00));
                            break;
                        }
                    }
                    break;

                default:
                    System.out.println(state);

            }
            if (check) {
                output.append(nowChr);
                check = false;
            }

            choNum = 0; //다음 계산을 위해 초기화
            joongNum1 = 0;
            joongNum2 = 0;
            joongNum = 0;
            jongNum1 = 0;
            jongNum2 = 0;
            jongNum = 0;
        }
        System.out.println(output.toString());
    }
}
