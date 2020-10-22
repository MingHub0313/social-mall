package com.zmm.mall.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallThirdPartyApplicationTests {


	@Autowired
	private OSSClient ossClient;


	@Test
	public void testAliBaBaCloudUploadFile() throws FileNotFoundException {
		// 上传文件流。
		InputStream inputStream = new FileInputStream("D:\\900045\\我的文档\\My Pictures\\3321885109341016.jpg");
		ossClient.putObject("mall-zmm", "3321885109341016.jpg", inputStream);

		// 关闭OSSClient。
		ossClient.shutdown();
		System.out.println("AliBaBaCloud上传完成!!!");
	}

}
