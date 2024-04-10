package leets.leets_mate;

import leets.leets_mate.exception.InvalidInputException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeetsMateApplication {

    public static void main(String[] args) {
        LeetsMateApplication app = new LeetsMateApplication();
        app.run();
    }

    // 동작 함수입니다.
    public void run() {
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
        List<String> memberList = parseMembers();

        // 2. 인원 수 입력 받기
        System.out.println("최대 짝 수를 입력해주세요.");
        int size = getSize(memberNumber(memberList));

        // 3. 짝 추첨 -> 출력
        executeDraw(memberList, size);
    }

    // 문자열로된 멤버들을 리스트로 분리하는 함수입니다.
    public List<String> parseMembers() {
        while(true) {
            try {
                return Arrays.stream(read().split(","))
                        .peek(this::checkHasNoEnglish)  // validation
                        .collect(Collectors.toList());
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // 총 멤버수를 반환합니다.
    public int memberNumber(List<String> members) {
        return members.size();
    }

    // 멤버 문자열에 영어가 있는지 검사합니다. 영어가 있다면 예외 출력
    public void checkHasNoEnglish(String members) throws InvalidInputException {
        if (!members.matches("^[ㄱ-ㅎㅏ-ㅣ가-힣]*$"))
            throw new InvalidInputException("[ERROR] 이름은 공백없이 한글로 입력해야 합니다. 다시 입력해주세요.");
    }

    // 멤버수와 최대 짝수 데이터가 유효한지 검사하는 함수입니다. 유효하지 않다면 예외 출력
    public int checkDataValidity(int memberCount, int maximumGroupSize) throws InvalidInputException {
        if (memberCount < maximumGroupSize)
            throw new InvalidInputException("[ERROR] 최대 짝 수는 이름의 갯수보다 클 수 없습니다. 다시 입력해주세요.");
        else if (maximumGroupSize < 1)
            throw new InvalidInputException("[ERROR] 최대 짝 수는 1보다 작을 수 없습니다. 다시 입력해주세요.");
        return maximumGroupSize;
    }

    // 랜덤 짝꿍 추첨하는 함수 입니다.
    public List<List<String>> generateRandomGroups(List<String> memberList, int maximumGroupSize) {
        Collections.shuffle(memberList);    // 리스트 셔플

        return IntStream.range(0, (int) Math.ceil((double) memberList.size() / maximumGroupSize))
                .mapToObj(i -> memberList.subList(i * maximumGroupSize,
                        Math.min((i + 1) * maximumGroupSize, memberList.size()) // 마지막 팀에 인원이 가득 차지 않았을 때 범위를 넘어가지 않기 위해 min 연산
                ))
                .collect(Collectors.toList());
    }

    // 결과를 프린트 하는 함수입니다.
    public void printResult(List<List<String>> result) {
        StringBuilder sb = new StringBuilder();
        for (List<String> teams : result) {
            Iterator<String> iter = teams.iterator();
            sb.append("[");
            while (iter.hasNext()) {
                sb.append(" " + iter.next() + " "); // 출력 포멧을 위한 공백 추가
                if (iter.hasNext()) {
                    sb.append("|");
                }
            }
            sb.append("]\n");
        }
        System.out.println(sb);
    }

    // 원하는 결과가 나올 때까지 추첨을 진행하는 함수입니다.
    public void executeDraw(List<String> memberList, int size) throws InvalidInputException {
        do {
            System.out.println("\n오늘의 짝 추천 결과입니다.");

            List<List<String>> result = generateRandomGroups(memberList, size); // 짝 추첨
            printResult(result);    // 출력
            System.out.println("추천을 완료했습니다.");
        } while (!isBreak());
    }

    // 추첨을 멈출 것인지 결정하는 함수입니다.
    public boolean isBreak() {
        while(true) {
            try {
                System.out.print("다시 구성하시겠습니까? (y or n): ");
                String input = read();

                if (input.equals("y")) {    // 재구성
                    System.out.println("--------------------------------");
                    return false;
                } else if (input.equals("n")) { // 고정
                    System.out.println("자리를 이동해 서로에게 인사해주세요.");
                    return true;
                } else {    // 그 외의 잘못된 응답 처리
                    throw new InvalidInputException("[ERROR] 응답은 y 혹은 n으로 입력해야 합니다.");
                }
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // 사용자의 모든 입력을 처리하는 함수입니다.
    public String read() {
        while(true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                return Optional.of(br.readLine())
                        .filter(input -> !input.trim().isEmpty())
                        .orElseThrow(() -> new InvalidInputException("[ERROR] 값을 입력해주세요.")); // validation
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException("[ERROR] 입력을 읽는 중 오류가 발생했습니다.", e);
            }
        }
    }

    // 그룹의 크기를 가져오는 함수입니다.
    public int getSize(int memberCnt) {
        while(true) {
            try {
                return checkDataValidity(memberCnt, Integer.parseInt(read()));
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}