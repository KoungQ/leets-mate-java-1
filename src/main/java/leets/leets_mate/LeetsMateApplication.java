package leets.leets_mate;

import leets.leets_mate.exception.InvalidInputException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class LeetsMateApplication {

    public static void main(String[] args) {
        LeetsMateApplication app = new LeetsMateApplication();
        app.run();
    }

    // 동작 함수입니다.
    public void run() {
        try {
            /** 설계
             *  1. 이름 입력 받기
             *      a. String -> List (parseMember)
             *      b. regex (checkDataValidity)
             *  2. 최대 짝(그룹) 인원 수 입력 받기
             *      a. 총 멤버 수 계산 (memberNumber)
             *      b. '멤버 수 > 팀당 인원 수' 검사 (checkDataValidity)
             *  3. 짝 랜덤 추첨 -> 출력 (generateRandomGroups -> printResult)
             */

            System.out.println("""
                    [Leets 오늘의 짝에게]를 시작합니다.

                    멤버의 이름을 입력해주세요. (, 로 구분)""");

            // 1. 이름 입력 받기
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));   // #1
            String memberStr = br.readLine();

            List<String> memberList = parseMembers(memberStr);
            for (String member : memberList) {
                checkHasNoEnglish(member);
            }

            // 2. 인원 수 입력 받기
            System.out.println("최대 짝 수를 입력해주세요.");
            int size = Integer.parseInt(br.readLine());
            checkDataValidity(memberNumber(memberList), size);

            boolean isFixed = false;
            while (!isFixed) {
                System.out.println("\n오늘의 짝 추천 결과입니다.");

                // 3. 짝 추첨 -> 출력
                List<List<String>> result = generateRandomGroups(memberList, size);
                printResult(result);

                System.out.print("""
                 추천을 완료했습니다.
                 다시 구성하시겠습니까? (y or n):\s""");
                isFixed = br.readLine().matches("n");   // n == true
                if (!isFixed) {
                    System.out.println("--------------------------------");
                }
            }
            System.out.println("자리를 이동해 서로에게 인사해주세요.");

        } catch (Exception e) {
            e.printStackTrace();    // 에러 추적
        }
    }

    // 문자열로된 멤버들을 리스트로 분리하는 함수입니다.
    public List<String> parseMembers(String members) {
        return Arrays.asList(members.split(","));
    }

    // 총 멤버수를 반환합니다.
    public int memberNumber(List<String> members) {
        return members.size();
    }

    // 멤버 문자열에 영어가 있는지 검사합니다. 영어가 있다면 예외 출력
    public void checkHasNoEnglish(String members) throws InvalidInputException {
        if (!members.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*"))
            throw new InvalidInputException("[ERROR] 이름은 한글로 입력해야 합니다");
    }

    // 멤버수와 최대 짝수 데이터가 유효한지 검사하는 함수입니다. 유효하지 않다면 예외 출력
    public void checkDataValidity(int memberCount, int maximumGroupSize) throws InvalidInputException {
        if (memberCount < maximumGroupSize)
            throw new InvalidInputException("[ERROR] 최대 짝 수는 이름의 갯수보다 클 수 없습니다");
        else if (maximumGroupSize < 1)
            throw new InvalidInputException("[ERROR] 최대 짝 수는 1보다 작을 수 없습니다");
    }

    // 랜덤 짝꿍 추첨하는 함수 입니다.
    public List<List<String>> generateRandomGroups(List<String> memberList, int maximumGroupSize) {
        List<List<String>> result = new ArrayList<>();  // 리턴 변수
        List<String> team = new ArrayList<>();

        Collections.shuffle(memberList);    // 리스트 셔플
        memberList
                .forEach(member -> {
                    team.add(" " + member + " ");    // 출력 포멧을 위해 공백 추가
                    if (team.size() >= maximumGroupSize) {
                        result.add(new ArrayList<>(team));  // Deep Copy
                        team.clear();
                    }
                });
        if (!team.isEmpty())     // 나머지 인원으로 구성된 팀이 있다면
            result.add(team);

        return result;
    }

    // 결과를 프린트 하는 함수입니다.
    public void printResult(List<List<String>> result) {
        StringBuilder sb = new StringBuilder(); // #2
        for (List<String> teams : result) {
            Iterator<String> iter = teams.iterator();
            sb.append("[");
            while (iter.hasNext()) {
                sb.append(iter.next());
                if (iter.hasNext()) {
                    sb.append("|");
                }
            }
            sb.append("]\n");
        }
        System.out.println(sb);
    }
}

/**
 * #1
 * Scanner
 * - 띄어쓰기, 개행으로 값 인식 -> 가공할 필요가 없음
 * - 데이터 타입에 맞게 입력 받을 수 있음
 * - 데이터 입력받을 때마다 사용자에게 즉시 전달 -> 느림
 * BufferedReader
 * - 추가 가공이 필요함
 * - 데이터 타입 변환 작업 필요
 * - 데이터를 버퍼에 담아두다가 입력이 끝나면 한 번에 사용자에게 전달 -> 빠름
 * <p>
 * 문제 조건에선 String으로 값을 한 번에 받고, 가공하는 작업을 parseMember 메서드에서 진행하게 돼있으므로
 * 입력 값의 길이가 길수록 유리한 BufferedReader 선택
 * <p>
 * <p>
 * #2
 * String
 * - String 객체 값은 불변
 * StringBuffer/StringBuilder
 * - 객체의 공간이 부족할 경우 버퍼의 크기를 유연하게 늘려주어 가변적 -> 문자열의 CUD가 빈번하다면 String보다 유리
 * <p>
 * 하지만 String의 + 연산자의 내부 구조는 StringBuilder 객체가 생성되고 연산을 하므로 합치는 연산은 String과 StringBuilder의 차이가 없다
 * 그러나 연산이 빈번히 발생할 경우 StringBuilder의 객체가 빈번히 생성-삭제를 해야하므로 메모리 측면에서 불리하다
 * 따라서 연산이 빈번할 경우엔 StringBuffer/StringBuilder를 사용하는 것이 좋다
 */