package io.systom.coin.service;

import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.exception.RequestException;
import io.systom.coin.model.Invitation;
import io.systom.coin.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * create joonwoo 2018. 8. 10.
 * 
 */
@Service
public class InvitationService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InvitationService.class);

    @Autowired
    private SqlSession sqlSession;

    @Value("${friends.maxSize}")
    private int defaultMaxInvitationSize;

    public Integer getUserMaxInvitationSize(String userId) {
        return defaultMaxInvitationSize;
    }

    public Invitation createInvitation(String userId) {
        List<Invitation> registerInvitations = selectInvitationList(userId);
        if (registerInvitations != null && registerInvitations.size() >= defaultMaxInvitationSize) {
            throw new RequestException("[FAIL] maximum invitations");
        }

        String refCode = generateRefCode();
        if (refCode == null) {
            throw new OperationException("[FAIL] generateRefCode ");
        }

        Invitation invitation = new Invitation();
        invitation.setUserId(userId);
        invitation.setRefCode(refCode);
        try {
            int changeRow = sqlSession.insert("invitation.createInvitation", invitation);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return invitation;
    }

    public Invitation updateRefUser(String refUserId, String refCode) {
        Invitation invitation = findInvitationByRefCode(refCode);
        if (invitation == null || !invitation.getStatus()) {
            throw new ParameterException("refCode");
        }
        invitation.setRefUserId(refUserId);
        try {
            int changeRow = sqlSession.update("invitation.updateRefUser", invitation);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return invitation;
    }

    public Invitation findInvitationByRefCode(String refCode) {
        try {
            return sqlSession.selectOne("invitation.findInvitationByRefCode", refCode);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public List<Invitation> selectInvitationList(String userId) {
        try {
            List<Invitation> invitations = sqlSession.selectList("invitation.selectInvitationList", userId);
            return invitations == null ? new ArrayList<>() : invitations;
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public String generateRefCode() {
        String refCode = null;
        Invitation invitation = null;
        for (int i=0; i < 100; i++) {
            refCode = StringUtils.genAsciiCode(8);
            invitation = findInvitationByRefCode(refCode);
            if (invitation == null || invitation.getStatus() == false) {
                break;
            } else {
                refCode = null;
            }
        }
        return refCode;
    }

    public int getFriendsCount(String userId) {
        List<Invitation> friends = selectInvitationList(userId);
        int friendsCount = 0;
        for (int i=0; i < friends.size(); i++) {
            if (!friends.get(i).getStatus()) {
                friendsCount++;
            }
        }
        return friendsCount;
    }



}