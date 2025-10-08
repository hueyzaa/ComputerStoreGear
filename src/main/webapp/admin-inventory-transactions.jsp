<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4">Quản lý Giao dịch Tồn kho</h1>

    <c:if test="${not empty param.message}">
        <div class="alert alert-success text-center" role="alert">
            ${param.message}
        </div>
    </c:if>
    <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger text-center" role="alert">
            ${requestScope.errorMessage}
        </div>
    </c:if>

    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle">
            <thead class="table-light">
                <tr>
                    <th>ID Giao dịch</th>
                    <th>Sản phẩm</th>
                    <th>Loại</th>
                    <th>Số lượng</th>
                    <th>Tham chiếu</th>
                    <th>Ghi chú</th>
                    <th>Ngày tạo</th>
                    <th>Người tạo</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="transaction" items="${requestScope.transactions}">
                    <tr>
                        <td>${transaction.transactionID}</td>
                        <td>${transaction.product.productName} (${transaction.productID})</td>
                        <td>
                            <span class="badge 
                                <c:choose>
                                    <c:when test="${transaction.transactionType == 'in'}">bg-success</c:when>
                                    <c:when test="${transaction.transactionType == 'out'}">bg-danger</c:when>
                                    <c:when test="${transaction.transactionType == 'adjustment'}">bg-warning</c:when>
                                </c:choose>
                            ">${transaction.transactionType}</span>
                        </td>
                        <td>${transaction.quantity}</td>
                        <td>${transaction.referenceType} - ${transaction.referenceID}</td>
                        <td>${transaction.notes}</td>
                        <td><fmt:formatDate value="${transaction.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                        <td>${transaction.createdByUser.username} (${transaction.createdBy})</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty requestScope.transactions}">
                    <tr>
                        <td colspan="8" class="text-center text-muted">Không có giao dịch tồn kho nào để hiển thị.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="admin-footer.jsp" />