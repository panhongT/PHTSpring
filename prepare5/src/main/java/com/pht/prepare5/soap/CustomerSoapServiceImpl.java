package com.pht.prepare5.soap;

import com.pht.prepare5.model.Customer;
import com.pht.prepare5.service.CustomerService;
import com.pht.framework.annotation.Inject;
import com.pht.framework.annotation.Service;
import com.pht.soap.Soap;

/**
 * 客户 SOAP 服务接口实现
 *
 * @author huangyong
 * @since 1.0.0
 */
@Soap
@Service
public class CustomerSoapServiceImpl implements CustomerSoapService {

    @Inject
    private CustomerService customerService;

    public Customer getCustomer(long customerId) {
        return customerService.getCustomer(customerId);
    }
}
