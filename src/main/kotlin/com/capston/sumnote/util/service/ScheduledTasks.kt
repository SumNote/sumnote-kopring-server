package com.capston.sumnote.util.service

import com.capston.sumnote.member.repository.MemberRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ScheduledTasks(private val memberRepository: MemberRepository) {

    @Scheduled(fixedRate = 86400000) // 매일 실행
    fun deactivateInactiveUsers() {
        val twoWeeksAgo = LocalDateTime.now().minusWeeks(2)
        memberRepository.findAll().forEach { member ->
            if (member.lastLoginAt?.isBefore(twoWeeksAgo) == true) {
                // 사용자 비활성화, 즉 자동 로그아웃
                member.isAutoLoginActive = false
                memberRepository.save(member)
            }
        }

    }
}