<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/views/fragments/entete.jsp"/>

<div class="page-head">
    <div>
        <h1>Étudiants</h1>
        <p class="muted">
            ${fn:length(etudiants)} étudiant(s)
            <c:if test="${not empty q}"> pour « <c:out value="${q}"/> »</c:if>
        </p>
    </div>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/etudiants?action=nouveau">+ Nouvel étudiant</a>
</div>

<c:if test="${not empty erreurBase}">
    <div class="alert alert-error"><c:out value="${erreurBase}"/></div>
</c:if>

<form class="search" action="${pageContext.request.contextPath}/etudiants" method="get">
    <input class="input" type="text" name="q" placeholder="Rechercher par nom ou filière…" value="<c:out value='${q}'/>">
    <button class="btn btn-primary" type="submit">Rechercher</button>
    <a class="btn btn-ghost" href="${pageContext.request.contextPath}/etudiants">Réinitialiser</a>
</form>

<div class="card table-wrap">
    <table class="table">
        <thead>
        <tr>
            <th>Nom</th><th>Filière</th>
            <th class="num">Maths</th><th class="num">Info</th><th class="num">Anglais</th>
            <th class="num">Moyenne</th><th>Mention</th><th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="e" items="${etudiants}">
            <tr>
                <td><c:out value="${e.nom}"/></td>
                <td><c:out value="${e.filiere}"/></td>
                <td class="num"><fmt:formatNumber value="${e.noteMath}" maxFractionDigits="2"/></td>
                <td class="num"><fmt:formatNumber value="${e.noteInfo}" maxFractionDigits="2"/></td>
                <td class="num"><fmt:formatNumber value="${e.noteAnglais}" maxFractionDigits="2"/></td>
                <td class="num"><strong><fmt:formatNumber value="${e.moyenneArrondie}" minFractionDigits="2" maxFractionDigits="2"/></strong></td>
                <td><span class="badge ${e.admis ? 'badge-ok' : 'badge-ko'}">${e.mention}</span></td>
                <td class="num"><a class="link-action" href="${pageContext.request.contextPath}/etudiants?action=modifier&id=${e.id}">Modifier</a></td>
            </tr>
        </c:forEach>
        <c:if test="${empty etudiants}">
            <tr><td colspan="8" class="empty">Aucun étudiant ne correspond à la recherche.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/views/fragments/pied.jsp"/>