package jp.co.sss.crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.crud.entity.Employee;
import jp.co.sss.crud.service.SearchAllEmployeesService;
import jp.co.sss.crud.service.SearchForEmployeesByDepartmentService;
import jp.co.sss.crud.service.SearchForEmployeesByEmpNameService;

/**
 * 検索機能コンポーネントに対応したコントローラー。list.htmlに対応する
 */
@Controller
public class ListController {

	/**
	 * 社員の全件検索サービス
	 */
	@Autowired
	SearchAllEmployeesService searchAllEmployeesService;
	/**
	 * 社員の名前検索サービス
	 */
	@Autowired
	SearchForEmployeesByEmpNameService searchForEmployeesByEmpNameService;
	/**
	 * 社員の部署検索サービス
	 */
	@Autowired
	SearchForEmployeesByDepartmentService searchForEmployeesByDepartmentService;

	/**
	 * 社員情報を全件検索した結果を出力
	 *
	 * @param model モデル
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public String findAll(Model model) {
		//検索した社員情報をmodelに追加
		model.addAttribute("employees", searchAllEmployeesService.execute());
		return "list/list";
	}

	/**
	 * 社員情報を社員名検索した結果を出力
	 *
	 * @param empName 検索対象の社員名
	 * @param model モデル
	 * @return 遷移先のビュー
	 */
	@RequestMapping(path = "/list/empName", method = RequestMethod.GET)
	public String findByEmpName(String empName, Model model) {
		// 検索実行
		List<Employee> employees = searchForEmployeesByEmpNameService.execute(empName);

		//検索した社員情報をmodelに追加
		model.addAttribute("employees", searchForEmployeesByEmpNameService.execute(empName));

		if (employees.isEmpty()) {
			model.addAttribute("message", "該当する社員情報がありません。");
		}
		return "list/list";
	}

	/**
	 * 社員情報を部署ID検索した結果を出力
	 *
	 * @param deptId 検索対象の部署ID
	 * @param model モデル
	 * @return 選先のビュー
	 */
	@RequestMapping(path = "/list/deptId", method = RequestMethod.GET)
	public String findByDeptId(Integer deptId, Model model) {
		//検索した社員情報をmodelに追加
		model.addAttribute("employees", searchForEmployeesByDepartmentService.execute(deptId));
		// 検索した部署IDを検索結果画面の初期値とするためmodelに保存
		model.addAttribute("selectedDeptId", deptId);
		return "list/list";
	}
}
