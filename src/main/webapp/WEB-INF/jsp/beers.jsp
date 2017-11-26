<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:import url="/WEB-INF/jsp/shared/header.jsp" />

	<table class="beer-list-table">
			<th> </th>
			<th>Name</th>
			<th>ABV</th>
			<th>IBU</th>
			<th>Type</th>
			<th>Brewery</th>
			<th></th>
			
		<c:forEach items="${allBeers}" var="beer">
			<tr>
				<td><c:url var="beerDetailLink" value="/beerDetails/${beer.id}"></c:url>
					<a href=" <c:out value='${beerDetailLink}'/> "><img src="<c:out value='${beer.imgUrl}'/>" style='width:40%' /></a></td>
				<td> <c:out value="${beer.name}"/> </td>
				<td> <c:out value="${beer.abv}"/>% </td>
				<td> <c:out value="${beer.ibu}"/> </td>
				<td> <c:out value="${beer.type}"/> </td>
				<td>
				<c:forEach items="${allBreweries}" var="brewery">
				<c:set var = "breweryId" value = "${beer.breweryId}"/>
				<c:if test="${brewery.id eq breweryId}">
					<c:out value="${brewery.getNameById(beer.breweryId)}"/> 
				</c:if>
				</c:forEach>
				</td>
				<td> <button type="button" class="btn btn-default">Review this Beer</button></td>
			</tr>	
		</c:forEach>
	</table>
	
<c:import url="/WEB-INF/jsp/shared/footer.jsp" />