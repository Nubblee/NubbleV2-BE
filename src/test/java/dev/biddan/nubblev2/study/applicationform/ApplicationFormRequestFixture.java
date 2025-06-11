package dev.biddan.nubblev2.study.applicationform;

import dev.biddan.nubblev2.study.applicationform.controller.dto.ApplicationFormApiRequest;

public class ApplicationFormRequestFixture {

    public static ApplicationFormApiRequest.Submit generateValidSubmitRequest() {
        return new ApplicationFormApiRequest.Submit(
                "알고리즘 학습 경험: 프로그래머스 Level 2까지 풀어본 경험이 있습니다.\n" +
                        "지원 동기: 혼자 공부하는 것보다 함께 문제를 풀며 성장하고 싶습니다.\n" +
                        "사용 가능한 프로그래밍 언어: Java, Python\n" +
                        "코딩테스트 풀이 경험: 프로그래머스 약 50문제, 백준 30문제\n" +
                        "참여 가능 시간: 평일 저녁 7시 이후, 주말 오전\n" +
                        "주당 투자 가능 시간: 10시간 정도\n"
        );
    }
}
