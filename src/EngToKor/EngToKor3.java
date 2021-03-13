package EngToKor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class EngToKor3 {
    //(choNum * 21 + joongNum) * 28 + jongNum + 0xAC00)
    //가능하면 현재 이후의 인덱스를 참조하려하지 말것 변수가 많아짐
    //첫가끝 초성 1100 중성 1161 종성 11A8
    //호환형한글자모 자음 0x3131 모음 0x314F

    public static void main(String[] args) {
        String input = "dkssudgktpdy";
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
        String nowChr;
        int choNum = 0;
        int joongNum1 = 0;
        int joongNum2 = 0;
        int jongNum1 = 0;
        int jongNum2 = 0;
        for (int i = 0; i <= input.length(); i++) {
            if (i != input.length()) {
                nowChr = input.substring(i, i + 1);
                if(nowChr.equals(" ")) check = true; //띄어쓰기시 true
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
                case "J": //마지막문자열에서 자음으로 끝날때
                    if (check || i == input.length()) { //유효한 다음 문자열이 없을 때 문자열을 완성
                        choNum = queue.poll();
                        output.append((char) (choNum + 0x1100));
                        state.delete(0, 1);
                    }
                    break;
                case "M": //모음만 단독으로 나올 때
                    joongNum1 = queue.poll();
                    output.append((char) (joongNum1 + 0x1161));
                    state.delete(0, 1);
                    break;
                case "JM": // 초성 + 중성으로 문자열이 끝날 때
                    if (check || i == input.length()) {
                        choNum = queue.poll();
                        joongNum1 = queue.poll();
                        state.delete(0,2);
                        output.append((char) ((choNum * 21 + joongNum1) * 28 + 0xAC00));
                    }
                    break;
                case "JMM": // 초성 + 중성 + 중성, MM이 joongEng에 없으면 JM 출력, i가 마지막 인덱스일때 JMM출력(중성모음에 포함되지 않은 MM은 미리 걸러짐)
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    joongNum2 = queue.poll();
                    queue.add(choNum);
                    queue.add(joongNum1);
                    queue.add(joongNum2);
                    if (check || i == input.length()) { // i가 마지막 인덱스일 때
                        for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                            if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                                queue.clear();
                                state.delete(0,3);
                                output.append((char) ((choNum * 21 + joongCheck) * 28 + 0xAC00));
                                break;
                            }
                        }
                    }

                    int finalJoongNum = joongNum1;
                    int finalJoongNum1 = joongNum2;
                    if (Arrays.stream(joongEng).noneMatch(n -> n.equals(joongEng[finalJoongNum] + joongEng[finalJoongNum1]))) { //MM이 중성모음에 없으면 JM출력
                        output.append((char) ((choNum * 21 + joongNum1) * 28 + 0xAC00));
                        queue.poll();
                        queue.poll();
                        output.append((char) (queue.poll() + 0x1161));
                        state.delete(0, 3);
                    }
                    break;

                case "JJ": //자음이 연속으로 나올때 가능성이 사라진 앞의 J는 출력
                    output.append((char) (queue.poll() + 0x1100));
                    state.delete(0, 1);
                    break;

                case "MJ":
                    output.append((char) (queue.poll() + 0x1161));
                    state.delete(0, 1);
                    break;

                case "JMJ": // 띄어쓰기나 i가 마지막 인덱스일 때 출력
                    if (check || i == input.length()) {
                        choNum = queue.poll();
                        joongNum1 = queue.poll();
                        jongNum1 = queue.poll();
                        state.delete(0,3);
                        for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                            if (jongEng[jongCheck].equals(choEng[jongNum1])) {
                                state.delete(0,3);
                                output.append((char) ((choNum * 21 + joongNum1) * 28 + jongCheck + 0xAC00));
                                break;
                            }
                        }

                    }

                    break;
                case "JMJM": //마지막에 모음이 올때 종의 가능성이 사라지므로 JM 출력
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    output.append((char) ((choNum * 21 + joongNum1) * 28 + 0xAC00));
                    state.delete(0, 2);
                    break;

                case "JMJJ": // 띄어쓰기나 i가 마지막 인덱스일 때 출력 혹은 JJ가 종배열에 존재하지 않을 경우 JMJ로 문자가 완성되므로 출력
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    jongNum1 = queue.poll();
                    jongNum2 = queue.poll();
                    queue.add(choNum);
                    queue.add(joongNum1);
                    queue.add(jongNum1);
                    queue.add(jongNum2);

                    if (check || i == input.length()) {
                        for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                            if (jongEng[jongCheck].equals(choEng[jongNum1] + choEng[jongNum2])) {
                                output.append((char) ((choNum * 21 + joongNum1) * 28 + jongCheck + 0xAC00));
                                queue.clear();
                                state.delete(0,4);
                                break;
                            }
                        }
                    }

                    int finalJongNum = jongNum1;
                    int finalJongNum1 = jongNum2;
                    if (Arrays.stream(jongEng).noneMatch(n -> n.equals(choEng[finalJongNum] + choEng[finalJongNum1]))) {

                        for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                            if (jongEng[jongCheck].equals(choEng[jongNum1])) {
                                output.append((char) ((choNum * 21 + joongNum1) * 28 + jongCheck + 0xAC00));
                                break;
                            }
                        }
                        queue.poll();
                        queue.poll();
                        queue.poll();
                        state.delete(0, 3);
                    }
                    break;

                case "JMJJJ": // 마지막에 J가 오므로 JMJJ로 문자가 완성되므로 출력
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    jongNum1 = queue.poll();
                    jongNum2 = queue.poll();
                    state.delete(0, 4);
                    for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                        if (jongEng[jongCheck].equals(choEng[jongNum1] + choEng[jongNum2])) {
                            output.append((char) ((choNum * 21 + joongNum1) * 28 + jongCheck + 0xAC00));
                        }
                    }
                    break;

                case "JMJJM": // 마지막에 M이 오므로 JJ중 앞은 종성 뒤는 초성
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
                case "JMMJ": // 띄어쓰기나 i가 마지막 인덱스일때 문자 출력
                    if (check || i == input.length()) {
                        choNum = queue.poll();
                        joongNum1 = queue.poll();
                        joongNum2 = queue.poll();
                        jongNum1 = queue.poll();
                        for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                            if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                                state.delete(0,4);
                                output.append((char) ((choNum * 21 + joongCheck) * 28 + jongNum1 + 0xAC00));
                            }
                        }
                    }
                    break;

                case "JMMJM": // 마지막에 M이 오므로 JMM으로 문자 출력
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    joongNum2 = queue.poll();
                    for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                        if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                            state.delete(0, 3);
                            output.append((char) ((choNum * 21 + joongCheck) * 28 + 0xAC00));
                        }
                    }
                    break;

                case "JMMJJ": // JMMJJ에서 JJ가 종성배열에 포함되지 않을 경우 JMMJ 출력
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    joongNum2 = queue.poll();
                    jongNum1 = queue.poll();
                    jongNum2 = queue.poll();
                    queue.add(choNum);
                    queue.add(joongNum1);
                    queue.add(joongNum2);
                    queue.add(jongNum1);
                    queue.add(jongNum2);


                    if (check || i == input.length()) {
                        int joongNum = 0;
                        for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                            if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                                joongNum = joongCheck;
                            }
                        }
                        for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                            if (jongEng[jongCheck].equals(jongEng[jongNum1] + jongEng[jongNum2])) {
                                queue.clear();
                                state.delete(0,5);
                                output.append((char) ((choNum * 21 + joongNum) * 28 + jongCheck + 0xAC00));
                                break;
                            }
                        }
                    }

                    int finalJongNum2 = jongNum1;
                    int finalJongNum3 = jongNum2;
                    if (Arrays.stream(joongEng).noneMatch(n -> n.equals(joongEng[finalJongNum2] + joongEng[finalJongNum3]))) {
                        for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                            if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                                output.append((char) ((choNum * 21 + joongCheck) * 28 + jongNum1 + 0xAC00));
                            }
                        }
                        queue.poll();
                        queue.poll();
                        queue.poll();
                        queue.poll();
                        state.delete(0, 4);
                    }
                    break;

                case "JMMJJJ": //마지막에 J가 오므로 JMMJJ로 문자 출력
                    choNum = queue.poll();
                    joongNum1 = queue.poll();
                    joongNum2 = queue.poll();
                    jongNum1 = queue.poll();
                    jongNum2 = queue.poll();
                    state.delete(0, 5);
                    int joongNum = 0;
                    for (int joongCheck = 0; joongCheck < joongEng.length; joongCheck++) {
                        if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                            joongNum = joongCheck;
                        }
                    }
                    for (int jongCheck = 0; jongCheck < jongEng.length; jongCheck++) {
                        if (jongEng[jongCheck].equals(jongEng[jongNum1] + jongEng[jongNum2])) {
                            output.append((char) ((choNum * 21 + joongNum) * 28 + jongCheck + 0xAC00));
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
                        if (joongEng[joongCheck].equals(joongEng[joongNum1] + joongEng[joongNum2])) {
                            output.append((char) (choNum * 21 + joongCheck) * 28 + jongNum1 + 0xAC00);
                        }
                    }
                    break;

                default:
                    System.out.println(state);

            }
            if(check){
                output.append(" ");
                check =false;
            }
            choNum = 0; //다음 계산을 위해 초기화
            joongNum1 = 0;
            joongNum2 = 0;
            jongNum1 = 0;
            jongNum2 = 0;
        }
        System.out.println(output.toString());
    }
}
