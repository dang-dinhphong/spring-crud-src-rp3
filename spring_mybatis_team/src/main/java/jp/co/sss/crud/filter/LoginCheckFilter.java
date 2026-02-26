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
 * ログインチェック用フィルタ
 * 
 * @author System Shared
 */
public class LoginCheckFilter extends HttpFilter {

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String path = request.getServletPath();

		// セッションからユーザー情報を取得
		HttpSession session = request.getSession(false);

		// ユーザーがNULLの場合、ログイン画面にリダイレクトする
		Employee loginUser = null;
		if (session != null) {
			Object userSession = session.getAttribute("user");
			if (userSession != null) {
				loginUser = (Employee) userSession;
			}
		}

		//静的部分許可
		if (path.contains("/css/") || path.contains("/js/") || path.contains("/img/")) {
			chain.doFilter(request, response);
			return;
		}

		boolean isLoginArea = path.equals("/") || path.equals("") || path.equals("/login");
		if (loginUser != null && isLoginArea) {
			response.sendRedirect(request.getContextPath() + "/list");
			return;
		}

		if (loginUser == null && isLoginArea) {
			chain.doFilter(request, response);
			return;
		}

		if (loginUser == null) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// 次の処理へ移行
		chain.doFilter(request, response);
		return;

	}
}
