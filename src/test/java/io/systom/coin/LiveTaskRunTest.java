package io.systom.coin;

import io.systom.coin.model.TraderTask;
import io.systom.coin.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

/*
 * create joonwoo 2018. 7. 25.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class LiveTaskRunTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(LiveTaskRunTest.class);
    @Autowired
    private TaskService taskService;

    @Test
    public void liveTaskRunTest() {
        TraderTask traderTask = new TraderTask();
        traderTask.setAction(TraderTask.ACTIONS.start.name());
        traderTask.setSessionType("live");
        traderTask.setInitCash(10000);
        traderTask.setGoodsId(9);
        String taskId = UUID.randomUUID().toString();
        traderTask.setId(taskId);

        traderTask.setUserId("joonwoo");
//        taskService.testTask(taskId);

        com.amazonaws.services.ecs.model.Task resultTask = taskService.liveTaskRun(traderTask);
        logger.info("결과: {}", resultTask);
    }
}