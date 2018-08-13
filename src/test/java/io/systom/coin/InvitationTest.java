package io.systom.coin;

import io.systom.coin.model.Invitation;
import io.systom.coin.service.InvitationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/*
 * create joonwoo 2018. 8. 10.
 * 
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class InvitationTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InvitationTest.class);

    @Autowired
    private InvitationService invitationService;

    @Test
    public void createInvitationTest() {

//        logger.info("결과: {}", StringUtils.genAsciiCode(8));
        Invitation invitation= invitationService.createInvitation("joonwoo");
        logger.info("result: {}", invitation);
    }

    @Test
    public void selectInvitationTest() {
        List<Invitation> invitations = invitationService.selectInvitationList("joonwoo");
        logger.info("{}", invitations);
    }


    @Test
    public void addRefTest() {
        String refCode = "27700eec";
        Invitation invitation = invitationService.findInvitationByRefCode(refCode);
        logger.info("{}", invitation);

        Invitation refUser = invitationService.updateRefUser("testuser", refCode);
        logger.info("{}", refUser);
//        List<Invitation> invitations = invitationService.selectInvitationList("joonwoo");
//        logger.info("{}", invitations);
    }


}