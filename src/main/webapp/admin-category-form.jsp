<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4"><c:choose><c:when test="${not empty requestScope.category}">Sửa Danh mục</c:when><c:otherwise>Thêm Danh mục mới</c:otherwise></c:choose></h1>

    <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger text-center" role="alert">
            ${requestScope.errorMessage}
        </div>
    </c:if>

    <div class="card p-4 shadow-sm">
        <form action="${pageContext.request.contextPath}/admin/categories/<c:choose><c:when test="${not empty requestScope.category}">edit</c:when><c:otherwise>add</c:otherwise></c:choose>" method="post">
            <c:if test="${not empty requestScope.category}">
                <input type="hidden" name="categoryId" value="${requestScope.category.categoryID}">
            </c:if>

            <div class="mb-3">
                <label for="categoryName" class="form-label">Tên Danh mục:</label>
                <input type="text" class="form-control" id="categoryName" name="categoryName" value="${requestScope.category.categoryName}" required>
            </div>
            <div class="mb-3">
                <label for="description" class="form-label">Mô tả:</label>
                <textarea class="form-control" id="description" name="description" rows="3">${requestScope.category.description}</textarea>
            </div>
            <div class="mb-3">
                <label for="parentCategoryId" class="form-label">Danh mục cha:</label>
                <select class="form-select" id="parentCategoryId" name="parentCategoryId">
                    <option value="">-- Không có --</option>
                    <c:forEach var="parentCat" items="${requestScope.parentCategories}">
                        <c:if test="${parentCat.categoryID != requestScope.category.categoryID}"> <%-- Prevent a category from being its own parent --%>
                            <option value="${parentCat.categoryID}" <c:if test="${parentCat.categoryID == requestScope.category.parentCategoryID}">selected</c:if>>${parentCat.categoryName}</option>
                        </c:if>
                    </c:forEach>
                </select>
            </div>
            <div class="form-check mb-3">
                <input class="form-check-input" type="checkbox" id="isActive" name="isActive" <c:if test="${requestScope.category.active}">checked</c:if>>
                <label class="form-check-label" for="isActive">Hoạt động</label>
            </div>

            <button type="submit" class="btn btn-primary w-100"><c:choose><c:when test="${not empty requestScope.category}">Cập nhật Danh mục</c:when><c:otherwise>Thêm Danh mục</c:otherwise></c:choose></button>
        </form>
        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">Quay lại danh sách danh mục</a>
        </div>
    </div>
</div>

<jsp:include page="admin-footer.jsp" />