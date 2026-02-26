package jp.co.sss.crud.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jp.co.sss.crud.entity.Employee;

/**
 * 権限認証用フィルタ
 * 
 * @author System Shared
 */
public class AdminAccountCheckFilter extends HttpFilter {

	/**
	 * ユーザ権限フィルタメソッド
	 * 
	 * @return アクセスを通過する。拒否される場合はログイン画面にリダイレクト
	 * 
	 */
	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// URIと送信方式を取得する
		String requestURI = request.getRequestURI();
		String requestMethod = request.getMethod();

		// セッションからユーザー情報を取得
		HttpSession session = request.getSession();
		Employee loginUser = (Employee) session.getAttribute("user");

		// 完了画面はフィルターを通過させる
		if (requestURI.contains("/complete") && requestMethod.equals("GET")) {
			chain.doFilter(request, response);
			return;
		}

		// NullPointerExceptionを防ぐ
		if (loginUser == null) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// isAuthorizedメソッドはfalseになった場合、セッションを終了し、ログイン画面にリダイレクト + 警告を出す
		if (!isAuthorized(request, loginUser)) {
			session.invalidate();
			response.sendRedirect(request.getContextPath() + "?showAlert=true");
			return;
		}

		chain.doFilter(request, response);
	}

	/**
	 * ユーザ権限を判定
	 * 
	 * @param request リクエスト
	 * @param user （ログイン中の）ユーザ
	 * @return 管理者はすべての機能にアクセス許可
	 * 一般権限は限られた機能のみ許可
	 */
	private boolean isAuthorized(HttpServletRequest request, Employee user) {
		Integer authority = user.getAuthority();
		String URI = request.getRequestURI();

		// 管理者権限をすべて許可
		if (authority == 2)
			return true;

		// 管理者以外は社員新規登録・削除をブロック
		if (URI.contains("/regist") || URI.contains("/delete")) {
			return false;
		}

		// セッションID = ユーザIDの場合のみ更新
		if (URI.contains("/update")) {
			String targetId = request.getParameter("empId");

			if (targetId == null || !String.valueOf(user.getEmpId()).equals(targetId)) {
				return false;
			}
		}

		return true;
	}

}
