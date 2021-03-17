<html>
<head>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div>
    <table>
        <tr>
            <th>Service</th>
            <th>Deployed version</th>
            <th>Artifact</th>
            <th>Name</th>
            <th>Time</th>
            <th>Group</th>
        </tr>
        <#list hosts?keys as prop>
            <tr>
                <td>${prop}</td>
                <td>${hosts[prop].version!}</td>
                <td>${hosts[prop].artifact!}</td>
                <td>${hosts[prop].name!}</td>
                <td>${hosts[prop].time!}</td>
                <td>${hosts[prop].group!}</td>
            </tr>
        </#list>
    </table>
</div>
</body>
</html>