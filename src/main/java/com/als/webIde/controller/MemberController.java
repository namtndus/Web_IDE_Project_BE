package com.als.webIde.controller;

import com.als.webIde.DTO.request.UserId;
import com.als.webIde.DTO.request.UserInfo;
import com.als.webIde.DTO.request.UserNickName;
import com.als.webIde.domain.entity.Member;
import com.als.webIde.domain.entity.MemberSetting;
import com.als.webIde.domain.repository.MemberRepositpory;
import com.als.webIde.domain.repository.MemberSettingRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class MemberController {

    private final MemberRepositpory memberRepositpory;
    private final MemberSettingRepository memberSettingRepository;

    @GetMapping("/idcheck")
    public ResponseEntity<String> checkedUserId(@RequestBody UserId userId){
        List<Member> memberByUserId = memberRepositpory.findMemberByUserId(userId.getUserId());
        if (memberByUserId.isEmpty()){
            return ResponseEntity.ok("사용할 수 있는 아이디입니다.");
        }
        return ResponseEntity.status(400).body("사용할 수 없는 아이디입니다.");
    }

    @GetMapping("/nicknamecheck")
    public ResponseEntity<String> checkedUserNickName(@RequestBody UserNickName nickName){
        List<MemberSetting> findNickName = memberSettingRepository.findMemberSettingByNickname(nickName.getUserNickName());

        if (findNickName.isEmpty()){
            return ResponseEntity.ok("사용할 수 있는 닉네임입니다.");
        }

        return ResponseEntity.status(400).body("사용할 수 없는 닉네임입니다.");
    }

    @Transactional
    @GetMapping("/signup")
    public ResponseEntity<String> singUp(@RequestBody UserInfo userInfo){
        if(!userInfo.getPassword().equals(userInfo.getPasswordConfirm())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        Member saveUser = Member.builder()
                .userId(userInfo.getUserId())
                .password(userInfo.getPassword()).build();
        Member member = memberRepositpory.save(saveUser);
        MemberSetting saveUserSetting = MemberSetting.builder()
                .member(member)
                .nickname(userInfo.getNickname()).build();

        memberSettingRepository.save(saveUserSetting);
        return ResponseEntity.ok("ok");
    }

}
