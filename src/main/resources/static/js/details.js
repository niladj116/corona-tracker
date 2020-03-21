    google.charts.load('current', {'packages':['corechart']});
          google.charts.setOnLoadCallback(drawChart);

          function drawChart() {
             var chartData = [];
             chartData.push(['Year-Mon','New Cases']);
             chartData.push(['2020-Jan',  500]);
             chartData.push(['2020-Feb',  400]);
             chartData.push(['2020-Mar',  460]);

             var data = google.visualization.arrayToDataTable(chartData);
             var options = {
             title: 'Company Performance',
             curveType: 'function',
             legend: { position: 'bottom' }
            };

            var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

            chart.draw(data, options);
          }

        function goBackToHome(event) {
            window.location.replace("/");
        }
