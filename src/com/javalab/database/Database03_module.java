package com.javalab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database03_module {
	
	// [멤버 변수]
	// 1. oracle 드라이버 이름 문자열 상수
	public static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
	// 2. oracle 데이터베이스 접속 경로(url) 문자열 상수
	public static final String DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
	// 3. 데이터베이스 접속 객체
	public static Connection con = null;
	// 4. query 실행 객체
	public static PreparedStatement pstmt = null;
	// 5. select 결과 저장 객체
	public static ResultSet rs = null;
	// 6. oracle 계정(id/pwd)
	public static String oracleId = "tempdb";
	// 데이터베이스 비밀번호
	public static String oraclePwd = "1234";

	// main 메소드가 간결해짐.
	public static void main(String[] args) {

		// 1. 디비 접속 메소드 호출
		connectDB();

		// 2. 쿼리문 실행 메소드 호출
		// - (여기서는 커넥션 객체 자원을 반납하지 않는다.)
		selectAllProduct();
		
		// 3. 특정 카테고리에 소속된 상품들만 조회하는 메소드
		// - (여기서는 커넥션 객체 자원을 반납한다.)
		String categoryName = "전자제품";
		selectProductsByCategory(categoryName);

	} // end main

	private static void selectProductsByCategory(String categoryName) {
		try {
			
			// 1. 쿼리문
			String sql = "select c.category_id, c.category_name, p.product_id, p.product_name,";
			sql += " p.price, to_char(receipt_date, 'yyyy-mm-dd') as receipt_date";
			sql += " from category c left outer join product p on c.category_id = p.category_id";
			sql += " where c.category_name = ?";
			sql += " order by c.category_id, p.product_id desc";
			
			// 2. 조건
			pstmt = con.prepareStatement(sql);
			System.out.println("3. stmt 객체 생성 성공 : ");
			pstmt.setString(1, categoryName);
			
			// 3. 쿼리 실행하고 결과를 ResultSe으로 반환받음
			// - 조회된 결과가 Result 객체에 담겨옴
			rs = pstmt.executeQuery();
			System.out.println();

			// 4. rs.next()의 의미 설명
			while (rs.next()) {
				System.out.println(rs.getString("category_id") + "\t" + rs.getString("category_name") + "\t"
						+ rs.getString("product_id") + "\t" + rs.getString("product_name") + "\t"
						+ rs.getString("price") + "\t" + rs.getString("receipt_date"));
			}
		}catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				System.out.println("자원해제 ERR! : " + e.getMessage());
			}
		}
	} // end selectProductsByCategory

	// 전체 상품 조회 메소드
	private static void selectAllProduct() {
		try {
		// 3. 쿼리문
		String sql = "select c.category_id, c.category_name, p.product_id, p.product_name,";
		sql += " p.price, to_char(receipt_date, 'yyyy-mm-dd') as receipt_date";
		sql += " from category c left outer join product p on c.category_id = p.category_id";
		sql += " order by c.category_id, p.product_id desc";
		
		pstmt = con.prepareStatement(sql);
		System.out.println("3. stmt 객체 생성 성공 : ");

		
		rs = pstmt.executeQuery(sql);
		System.out.println();
		
		while (rs.next()) {
			System.out.println(rs.getString("category_id") + "\t" + rs.getString("category_name") + "\t"
					+ rs.getString("product_id") + "\t" + rs.getString("product_name") + "\t"
					+ rs.getString("price") + "\t" + rs.getString("receipt_date"));
		}
		
		}catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				 /* [커넥션 객체는 계속해서 다음 메소드에서 써야되기 때문에 닫지 않음.]
				  * if (con != null) {
					con.close();
				}*/
			} catch (SQLException e) {
				System.out.println("자원해제 ERR! : " + e.getMessage());
			}
		}
	} // end selectProduct()
	
	// 디비 접속 메소드
	private static void connectDB() {
		try {

			Class.forName(DRIVER_NAME);
			System.out.println("1. 드라이버 로드 성공!");
			con = DriverManager.getConnection(DB_URL, oracleId, oraclePwd);
			System.out.println("2. 커넥션 객체 생성 성공!");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 ERR! : " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		}
	} // end connectDB()
	
} // end class