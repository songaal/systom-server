package io.systom.coin.service;

import io.systom.coin.model.Invoice;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * create joonwoo 2018. 8. 8.
 * 
 */
@Service
public class InvoiceService {

    @Autowired
    private SqlSession sqlSession;

    public List<Invoice> retrieveInvoice(String userId) {
        return sqlSession.selectList("invoice.retrieveInvoice", userId);
    }


}