<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/views/fragments/entete.jsp"/>

<c:set var="nouveau" value="${etudiant.id == 0}"/>

<%-- Notes de l'étudiant existant, affichées « 15 » plutôt que « 15.0 ».
     Pour un nouvel étudiant, ces variables n'existent pas : les champs restent vides. --%>
     
<c:if test="${not nouveau and empty erreurs}">
    <fmt:formatNumber var="vMath" value="${etudiant.noteMath}" maxFractionDigits="2" groupingUsed="false"/>
    <fmt:formatNumber var="vInfo" value="${etudiant.noteInfo}" maxFractionDigits="2" groupingUsed="false"/>
    <fmt:formatNumber var="vAnglais" value="${etudiant.noteAnglais}" maxFractionDigits="2" groupingUsed="false"/>
</c:if>

<div class="page-head">
    <div>
        <h1>${nouveau ? 'Nouvel étudiant' : 'Modifier l’étudiant'}</h1>
        <p class="muted">Notes sur 20 — la virgule est acceptée (ex. 12,5).</p>
    </div>
</div>

<c:if test="${not empty erreurs}">
    <div class="alert alert-error">
        <strong>Le formulaire contient ${erreurs.size()} erreur(s) :</strong>
        <ul>
            <c:forEach var="err" items="${erreurs}"><li><c:out value="${err}"/></li></c:forEach>
        </ul>
    </div>
</c:if>

<form class="card form" method="post" action="${pageContext.request.contextPath}/etudiants">
    <input type="hidden" name="action" value="enregistrer">
    <c:if test="${not nouveau}">
        <input type="hidden" name="id" value="${etudiant.id}">
    </c:if>

    <div class="field field-wide">
        <label for="nom">Nom complet</label>
        <input class="input" id="nom" name="nom"
               value="<c:out value='${empty erreurs ? etudiant.nom : param.nom}'/>">
    </div>

    <div class="field field-wide">
        <label for="filiere">Filière</label>
        <input class="input" id="filiere" name="filiere" list="filieres"
               value="<c:out value='${empty erreurs ? etudiant.filiere : param.filiere}'/>">
        <datalist id="filieres">
            <option value="Informatique"><option value="Génie civil"><option value="Mécanique">
        </datalist>
    </div>

    <div class="field">
        <label for="noteMath">Mathématiques <span class="muted">· coef. 3</span></label>
        <input class="input" id="noteMath" name="noteMath" inputmode="decimal"
               value="<c:out value='${empty erreurs ? vMath : param.noteMath}'/>">
    </div>
    <div class="field">
        <label for="noteInfo">Informatique <span class="muted">· coef. 3</span></label>
        <input class="input" id="noteInfo" name="noteInfo" inputmode="decimal"
               value="<c:out value='${empty erreurs ? vInfo : param.noteInfo}'/>">
    </div>
    <div class="field">
        <label for="noteAnglais">Anglais <span class="muted">· coef. 2</span></label>
        <input class="input" id="noteAnglais" name="noteAnglais" inputmode="decimal"
               value="<c:out value='${empty erreurs ? vAnglais : param.noteAnglais}'/>">
    </div>

    <div class="form-actions">
        <a class="btn btn-ghost" href="${pageContext.request.contextPath}/etudiants">Annuler</a>
        <button class="btn btn-primary" type="submit">Enregistrer</button>
    </div>
</form>

<jsp:include page="/WEB-INF/views/fragments/pied.jsp"/>