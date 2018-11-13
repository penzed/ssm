package com.soecode.lyf.web;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.soecode.lyf.dto.AppointExecution;
import com.soecode.lyf.dto.Result;
import com.soecode.lyf.entity.Book;
import com.soecode.lyf.enums.AppointStateEnum;
import com.soecode.lyf.exception.NoNumberException;
import com.soecode.lyf.exception.RepeatAppointException;
import com.soecode.lyf.service.BookService;

@Controller
@RequestMapping("/book") // url:/模块/资源/{id}/细分 /seckill/list
public class BookController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BookService bookService;

	/*
value的值是数组，可以将多个url映射到同一个方法:
@RequestMapping({
			"/request1",
			"/request2",
			"/request3"
	})*/
	/*@RequestMapping(value = "/list", method = RequestMethod.GET)
	private String list(Model model) {
		List<Book> list = bookService.getList();
		model.addAttribute("list", list);
		// list.jsp + model = ModelAndView
		return "list";// WEB-INF/jsp/"list".jsp
	}*/

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    private Result<List> list() {
        List<Book> list = bookService.getList();
        Result<List> listResult = new Result<>();
        listResult.setData(list);
        listResult.setSuccess(true);
        return listResult;
    }

	@RequestMapping(value = "/{bookId}/detail", method = RequestMethod.GET)
    @ResponseBody
	private Result<Book> detail(@PathVariable("bookId") Long bookId, Model model) {
        Result<Book> bookResult = new Result<>();
		Book book = bookService.getById(bookId);
		if (book == null) {
			return bookResult;
		}
        bookResult.setData(book);
		bookResult.setSuccess(true);
		return bookResult;
	}

	// ajax json
	@RequestMapping(value = "/{bookId}/appoint", method = RequestMethod.POST, produces = {
			"application/json; charset=utf-8" })
	@ResponseBody
	private Result<AppointExecution> appoint(@PathVariable("bookId") Long bookId, @RequestParam("studentId") Long studentId) {
		if (studentId == null || studentId.equals("")) {
			return new Result<>(false, "学号不能为空");
		}
		AppointExecution execution = null;
		try {
			execution = bookService.appoint(bookId, studentId);
		} catch (NoNumberException e1) {
			execution = new AppointExecution(bookId, AppointStateEnum.NO_NUMBER);
		} catch (RepeatAppointException e2) {
			execution = new AppointExecution(bookId, AppointStateEnum.REPEAT_APPOINT);
		} catch (Exception e) {
			execution = new AppointExecution(bookId, AppointStateEnum.INNER_ERROR);
		}
		return new Result<AppointExecution>(true, execution);
	}

	/*使用@RequestParam常用于处理简单类型的绑定。
	value：参数名字，即入参的请求参数名字，如value=“item_id”表示请求的参数区中的名字为item_id的参数的值将传入；
	required：是否必须，默认是true，表示请求中一定要有相应的参数，否则将报；
	TTP Status 400 - Required Integer parameter 'XXXX' is not present
	defaultValue：默认值，表示如果请求中没有同名参数时的默认值
*/
}


/*
		在controller方法形参上可以定义request和response，使用request或response指定响应结果：
		1、使用request转向页面，如下：
		request.getRequestDispatcher("页面路径").forward(request, response);

		2、也可以通过response页面重定向：
		response.sendRedirect("url")

		3、也可以通过response指定响应结果，例如响应json数据如下：
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write("json串");*/
