package com.shopme.admin.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.OrderDetail;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class OrderDetailRepositoryTests {
	@Autowired
	private OrderDetailRepository repo;
	
	@Test
	public void testFindByOrderDetailTimeBetween() throws ParseException {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse("2021-01-15");
		Date endTime = dateFormatter.parse("2021-01-31");
		
		List<OrderDetail> listOrderDetails = repo.findByOrderDetailCategoryNameTimeBetween(startTime, endTime);
		
		int count = 1;
		for (OrderDetail aDetail : listOrderDetails) {
			System.out.println(count++ + " | " + aDetail.getProduct().getCategory().getName()
					+ " | " + aDetail.getSubtotal() + " | " + aDetail.getCost() + " | " + aDetail.getShip());
		}
	}

}
