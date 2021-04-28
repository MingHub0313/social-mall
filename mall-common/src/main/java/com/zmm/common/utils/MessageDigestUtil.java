package com.zmm.common.utils;


import com.zmm.common.constant.Constants;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author 900045
 * @description:
 * @name MessageDigestUtil
 * @date By 2021-02-25 11:56:08
 */
public class MessageDigestUtil {

	/**
	 * 先进行MD5摘要再进行Base64编码获取摘要字符串
	 *
	 * @param str
	 * @return
	 */
	public static String base64AndMD5(String str) {
		if (str == null) {
			throw new IllegalArgumentException("inStr can not be null");
		}
		return base64AndMD5(toBytes(str));
	}

	/**
	 * 先进行MD5摘要再进行Base64编码获取摘要字符串
	 *
	 * @return
	 */
	public static String base64AndMD5(byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException("bytes can not be null");
		}
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(bytes);
			final Base64 base64 = new Base64();
			final byte[] enBytes = base64.encode(md.digest());
			return new String(enBytes);
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("unknown algorithm MD5");
		}
	}

	/**
	 * UTF-8编码转换为ISO-9959-1
	 *
	 * @param str
	 * @return
	 */
	public static String utf8ToIso88591(String str) {
		if (str == null) {
			return str;
		}

		try {
			return new String(str.getBytes("UTF-8"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * ISO-9959-1编码转换为UTF-8
	 *
	 * @param str
	 * @return
	 */
	public static String iso88591ToUtf8(String str) {
		if (str == null) {
			return str;
		}

		try {
			return new String(str.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * String转换为字节数组
	 *
	 * @param str
	 * @return
	 */
	private static byte[] toBytes(final String str) {
		if (str == null) {
			return null;
		}
		try {
			return str.getBytes(Constants.ENCODING);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static void verifyCode(String username, HttpServletResponse response) throws IOException {
		//1 绘制图片
		//字体只显示大写，去掉了1,0,i,o几个容易混淆的字符
		String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

		int IMG_WIDTH = 72;
		int IMG_HEIGTH = 27;
		Random random = new Random();
		//创建图片
		BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGTH, BufferedImage.TYPE_INT_RGB);
		//画板
		Graphics g = image.getGraphics();
		//填充背景
		g.setColor(Color.WHITE);
		g.fillRect(1,1,IMG_WIDTH-2,IMG_HEIGTH-2);

		//设置字体
		g.setFont(new Font("楷体",Font.BOLD,25));

		/** #1 提供变量保存随机字符数据 */
		StringBuilder sb = new StringBuilder();

		//绘制4个字符
		for(int i = 1 ; i <= 4 ; i ++){
			//随机颜色
			g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
			//随机生成4个字符
			int len = random.nextInt(VERIFY_CODES.length());
			String str = VERIFY_CODES.substring(len,len+1);

			/** #2 存放随机字符串 */
			sb.append( str );

			g.drawString(str, IMG_WIDTH / 6 * i , 22 );
		}
		/** #3 获得随机字符串 */
		String randomStr = sb.toString();

		/** #4 将结果保存到redis */
		String key = "login" + username;
		//stringRedisTemplate.opsForValue().set(key, randomStr , 5 , TimeUnit.MINUTES)


		// 生成随机干扰线
		for (int i = 0; i < 30; i++) {
			//随机颜色
			g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
			int x = random.nextInt(IMG_WIDTH - 1);
			int y = random.nextInt(IMG_HEIGTH - 1);
			int x1 = random.nextInt(12) + 1;
			int y1 = random.nextInt(6) + 1;
			g.drawLine(x, y, x - x1, y - y1);
		}
		//2 将图片响应给浏览器
		ImageIO.write(image,"jpeg", response.getOutputStream());
	}
	
}
