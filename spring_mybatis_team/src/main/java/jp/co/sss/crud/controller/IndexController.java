package jp.co.sss.crud.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.crud.entity.Employee;
import jp.co.sss.crud.form.LoginForm;
import jp.co.sss.crud.service.LoginResult;
import jp.co.sss.crud.service.LoginService;

@Controller
public class IndexController {

	@Autowired
	HttpSession session;

	@Autowired
	LoginService loginService;

	/**
	 * ログイン画面の表示
	 * 
	 * @param loginForm
	 * @return 遷移先ビュー
	 */
	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String index(@ModelAttribute LoginForm loginForm) {
		Employee loginUser = (Employee) session.getAttribute("user");
		if (loginUser != null) {
			return "redirect:/list";
		}
		session.invalidate();
		return "index";
	}

	/**
	 * ログインコントローラー
	 * 
	 * @param loginForm
	 * @param result エラー検知オブジェクト
	 * @param session 
	 * @param model リクエストスコープの操作
	 * @return 遷移先ビュー
	 */
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult result, HttpSession session,
			Model model) {

		// 入力エラー
		if (result.hasErrors()) {
			return "index";
		}

		LoginResult loginResult = loginService.execute(loginForm);

		// ログイン成功
		if (loginResult.isLogin()) {
			// セッションにuser登録
			session.setAttribute("user", loginResult.getLoginUser());
			return "redirect:/list";
		} else {
			model.addAttribute("errMessage", loginResult.getErrorMsg());
			return "index";
		}

	}

	@RequestMapping(path = "/logout", method = RequestMethod.GET)
	public String logout() {
		session.invalidate();
		return "redirect:/";
	}

}
