<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/fragments/entete.jsp"/>


<h2>Liste des étudiants</h2>

<c:if test="${not empty erreurBase}"><p class="erreur"><c:out value="${erreurBase}"/></p></c:if>

<form action="${pageContext.request.contextPath}/etudiants" method="get">
    <input type="text" name="q" placeholder="Nom ou filiere..." value="<c:out value='${q}'/>">
    <button type="submit">Rechercher</button>
    <a href="${pageContext.request.contextPath}/etudiants">Réinitialiser</a>
</form>

<table border="1">
    <thead>
    <tr>
        <th>Nom</th><th>Filière</th><th>Maths</th><th>Info</th><th>Anglais</th>
        <th>Moyenne</th><th>Mention</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="e" items="${etudiants}">
        <tr>
            <td><c:out value="${e.nom}"/></td>
            <td><c:out value="${e.filiere}"/></td>
            <td>${e.noteMath}</td>
            <td>${e.noteInfo}</td>
            <td>${e.noteAnglais}</td>
            <td><strong><fmt:formatNumber value="${e.moyenneArrondie}" minFractionDigits="2" maxFractionDigits="2"/></strong></td>
            <td class="${e.admis ? 'admis' : 'ajourne'}">${e.mention}</td>
        </tr>
    </c:forEach>
    <c:if test="${empty etudiants}">
        <tr><td colspan="7">Aucun étudiant.</td></tr>
    </c:if>
    </tbody>
</table>



<jsp:include page="/WEB-INF/views/fragments/pied.jsp"/>