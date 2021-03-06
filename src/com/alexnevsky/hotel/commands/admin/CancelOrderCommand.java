package com.alexnevsky.hotel.commands.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alexnevsky.hotel.commands.ICommand;
import com.alexnevsky.hotel.controller.Controller;
import com.alexnevsky.hotel.dao.AbstractDAOFactory;
import com.alexnevsky.hotel.dao.OrderDAO;
import com.alexnevsky.hotel.dao.exception.DAOException;
import com.alexnevsky.hotel.manager.AttributesManager;
import com.alexnevsky.hotel.manager.ConfigurationManager;
import com.alexnevsky.hotel.manager.MessageManager;
import com.alexnevsky.hotel.model.Order;
import com.alexnevsky.hotel.model.enums.OrderStatusEnum;

/**
 * This admin's command cancels the order with given id.
 * 
 * @version 1.0 22.05.2011
 * @author Alex Nevsky
 */
public class CancelOrderCommand implements ICommand {

	static {
		logger = Logger.getLogger(CancelOrderCommand.class);
	}
	private static Logger logger;

	/**
	 * Updates order's status and redirects admin to the main page.
	 */
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) {
		String page = null;

		Long orderId = Long.valueOf(request.getParameter(AttributesManager.PARAM_NAME_ORDER_ID));

		logger.info("Admin '" + request.getSession().getAttribute(AttributesManager.PARAM_NAME_LOGIN) + "'. Execute "
				+ this.toString() + ". RemoteAddr: " + request.getRemoteAddr());

		try {
			AbstractDAOFactory daoFactory = Controller.getDAOFactory();

			OrderDAO orderDAO = daoFactory.getOrderDAO();

			Order order = null;
			order = orderDAO.find(orderId);

			logger.info("Admin '" + request.getSession().getAttribute(AttributesManager.PARAM_NAME_LOGIN)
					+ "'. Change order status to '" + OrderStatusEnum.CANCELLED.toString() + "' for '" + order + "'"
					+ ". RemoteAddr: " + request.getRemoteAddr());

			orderDAO.update(OrderStatusEnum.CANCELLED, orderId);

			request.setAttribute(AttributesManager.ATTRIBUTE_RESULT, MessageManager.RESULT_CANCEL_ORDER_MESSAGE);

		} catch (DAOException ex) {
			logger.error(ex, ex);
			request.setAttribute(AttributesManager.ATTRIBUTE_ERROR_MESSAGE, MessageManager.DAO_EXCEPTION_ERROR_MESSAGE);
			page = ConfigurationManager.getInstance().getProperty(ConfigurationManager.ERROR_PAGE_PATH);
			return page;
		}

		page = ConfigurationManager.getInstance().getProperty(ConfigurationManager.MAIN_PAGE_PATH);

		return page;
	}

	@Override
	public String toString() {
		return "CancelOrderCommand{" + '}';
	}
}