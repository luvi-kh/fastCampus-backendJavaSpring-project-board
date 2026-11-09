package com.fastcampus.projectboard.dto.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@SuppressWarnings("unchecked")
public record KakaoOAuth2Response(
        Long id,
        LocalDateTime connectedAt,
        Map<String, Object> properties,
        KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            Boolean profileNicknameNeedsAgreement,
            Profile profile,
            Boolean hasEmail,
            Boolean emailNeedsAgreement,
            Boolean isEmailValid,
            Boolean isEmailVerified,
            String email
    ) {
        public record Profile(String nickname) {
            public static Profile from(Map<String, Object> attributes) {
                if (attributes == null) return new Profile("카카오유저");
                return new Profile(String.valueOf(attributes.get("nickname")));
            }
        }

        public static KakaoAccount from(Map<String, Object> attributes) {
            if (attributes == null) return new KakaoAccount(true, new Profile("카카오유저"), false, true, false, false, null);

            // 💡 [핵심 교정] String.valueOf(null)이 "null" 문자열이 되는 버그를 완벽히 차단합니다.
            Object rawEmail = attributes.get("email");
            String resolvedEmail = (rawEmail == null) ? null : String.valueOf(rawEmail);

            return new KakaoAccount(
                    Boolean.valueOf(String.valueOf(attributes.get("profile_nickname_needs_agreement"))),
                    Profile.from((Map<String, Object>) attributes.get("profile")),
                    Boolean.valueOf(String.valueOf(attributes.get("has_email"))),
                    Boolean.valueOf(String.valueOf(attributes.get("email_needs_agreement"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_valid"))),
                    Boolean.valueOf(String.valueOf(attributes.get("is_email_verified"))),
                    resolvedEmail // ✅ 안전하게 필터링된 실제 null 또는 이메일 문자열 주입
            );
        }

        public String nickname() { return this.profile().nickname(); }
    }

    public static KakaoOAuth2Response from(Map<String, Object> attributes) {
        return new KakaoOAuth2Response(
                Long.valueOf(String.valueOf(attributes.get("id"))),
                LocalDateTime.parse(
                        String.valueOf(attributes.get("connected_at")),
                        DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault())
                ),
                (Map<String, Object>) attributes.get("properties"),
                KakaoAccount.from((Map<String, Object>) attributes.get("kakao_account"))
        );
    }

    // 💡 [추가 보완] 외부에서 이메일을 꺼낼 때 한 번 더 null 및 "null" 문자열 방어막을 쳐줍니다.
    public String email() {
        if (this.kakaoAccount() == null || this.kakaoAccount().email() == null || "null".equals(this.kakaoAccount().email())) {
            return null;
        }
        return this.kakaoAccount().email();
    }

    public String nickname() { return this.kakaoAccount().nickname(); }
}
