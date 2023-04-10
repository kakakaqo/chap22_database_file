package com.javalab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseMain {

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

		// 4. 가격이 25,000원 이상인 상품들의 이름과 가격을 조회하시오.
		selectProductGatherThan();

		// 5. 카테고리별로 카테고리명과 가격의 합계금액을 조회하되 금액이 큰 순서로
		selectProductGroupByCatrgory();

		// 6. 상품 추가 :: 카테고리 : 식료품 / 상품 ID : 기존 번호 + 1 상품명 : 양배추 / 가격 : 2000 / 입고일 :
		// 2022/07/10
		insertProduct();

		// 7. 상품 가격 변경(수정) 탱크로리 상품의 가격을 600000 으로 수정
		updateProduct();

		// 8. 자원반환
		closeResource(pstmt, rs);

	} // end main

	private static void updateProduct() {
		try {
			String product_name = "탱크로리";
			int price = 600000;

			String sql = "update product set price = ?";
			sql += " where product_name = ?";

			// 5. 조회조건이 넘어가면 무조건 PreparedStatement 객체
			// PreparedStatement 객체 얻음
			pstmt = con.prepareStatement(sql);

			// 5.1 쿼리문에 인자 전달
			pstmt.setInt(1, price); // 가격 인자 전달
			pstmt.setString(2, product_name); // 조회조건 상품명 전달

			// 6. PreparedStatement 객체의 executeQuery() 메소드를 통해서 쿼리 실행
			// 데이터 베이스에서 처리된 결과 반환됨
			int resultRows = pstmt.executeUpdate();
			if (resultRows > 0) {
				System.out.println("수정 성공");
			} else {
				System.out.println("수정 실패");
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			closeResource(pstmt, rs);
		}
	} // end updateProduct()

	private static void insertProduct() {
		try {

			int product_id = 22;
			String product_name = "양배추";
			int price = 2000;
			int category_id = 5;
			String receiptDate = "2022/07/10";

			// 4. PreparedStatement 객체에 사용할 SQL문 생성
			String sql = "insert into product(product_id, product_name, price, category_id, receipt_date) ";
			sql += "values(?, ?, ?, ?, to_date(?, 'YYYY/MM/DD'))";

			pstmt = con.prepareStatement(sql);

			// 5.1 쿼리문에 인자 전달
			pstmt.setInt(1, product_id);
			pstmt.setString(2, product_name);
			pstmt.setInt(3, price);
			pstmt.setInt(4, category_id);
			pstmt.setString(5, receiptDate);

			int resultRows = pstmt.executeUpdate();
			if (resultRows > 0) {
				System.out.println("저장 성공");
			} else {
				System.out.println("저장 실패");
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			closeResource(pstmt, rs);
		}

	} // end insertProduct()

	private static void selectProductGroupByCatrgory() {
		try {

			String sql = "SELECT c.category_id, c.category_name, SUM(p.price) as price";
			sql += " FROM CATEGORY C, PRODUCT P";
			sql += " WHERE c.category_id = p.category_id";
			sql += " GROUP BY c.category_id, c.category_name";
			sql += " ORDER BY SUM(P.PRICE) DESC";

			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery(sql);
			System.out.println();

			// 4. rs.next()의 의미 설명
			while (rs.next()) {
				System.out.println(
						rs.getString("category_id") + "\t" + rs.getString("category_name") + "\t" + rs.getInt("price"));
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			closeResource(pstmt, rs);
		}

	} // end selectProductGroupByCatrgory

	private static void selectProductGatherThan() {
		int price = 25000;

		try {
			String sql = "SELECT p.product_name, p.price";
			sql += " FROM PRODUCT P";
			sql += " WHERE P.PRICE >= ?";
			sql += " ORDER BY PRICE DESC";

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, price);

			rs = pstmt.executeQuery();
			System.out.println();

			// 4. rs.next()의 의미 설명
			while (rs.next()) {
				System.out.println(rs.getString("product_name") + "\t" + rs.getInt("price"));
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			closeResource(pstmt, rs);
	}
} // end selectProductGatherThan()

	// 자원 반환 메소드
	private static void closeResource(PreparedStatement pstmt, ResultSet rs) {
		try {
			
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}

		} catch (SQLException e) {
			System.out.println("자원해제 ERR! : " + e.getMessage());
		}

	} // end closeResource()

	// 특정 카테고리에 소속된 상품들만 조회하는 메소드
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
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {closeResource(pstmt, rs);
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

			rs = pstmt.executeQuery(sql);
			System.out.println();

			while (rs.next()) {
				System.out.println(rs.getString("category_id") + "\t" + rs.getString("category_name") + "\t"
						+ rs.getString("product_id") + "\t" + rs.getString("product_name") + "\t"
						+ rs.getString("price") + "\t" + rs.getString("receipt_date"));
			}

		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
				closeResource(pstmt, rs);
	} // end selectProduct()
}
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