    google.charts.load('current', {'packages':['corechart']});
          google.charts.setOnLoadCallback(drawChart);

          function drawChart() {
             var chartData = [];
             chartData.push(['Date','Confirmed','Death']);
//             chartData.push(['Date','Confirmed','Death','Recovered']);

             var casesIncreaseByDates = confirmedByCountryByDate.substring(1, confirmedByCountryByDate.length-1).split(',');
             for(cases of casesIncreaseByDates) {
                var item = cases.split('=');
                chartData.push([item[0],  parseInt(item[1])]);
             }

             casesIncreaseByDates = deathByCountryByDate.substring(1, deathByCountryByDate.length-1).split(',');
             var i=1;
             for(cases of casesIncreaseByDates) {
                var item = cases.split('=');
                var data = chartData[i];
                data.push(parseInt(item[1]));
                chartData[i] = data;
                i++;
             }

//             casesIncreaseByDates = recoveredByCountryByDate.substring(1, recoveredByCountryByDate.length-1).split(',');
//              var i=1;
//              for(cases of casesIncreaseByDates) {
//                 var item = cases.split('=');
//                 var data = chartData[i];
//                 data.push(parseInt(item[1]));
//                 chartData[i] = data;
//                 i++;
//              }

             document.getElementById('displayName').innerHTML += displayCountryName(countryName);
             var data = google.visualization.arrayToDataTable(chartData);
             var options = {
             title: 'Trend of Daily Cases',
//             titlePosition: in,
             curveType: 'function',
             legend: { position: 'bottom' }
            };

            var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));
//            google.visualization.events.addListener(chart, 'ready', function() {
//                var chartLayout = chart.getChartLayoutInterface();
//                var chartPosition = document.getElementById('curve_chart').getBoundingClientRect();
//                var titleLeft = 24;
//                var titlePosition = chartLayout.getBoundingBox('title');
//                 document.getElementById("countryFlagImage").style.left = (chartPosition.left + titlePosition.left - titleLeft) + 'px',
//                 document.getElementById("countryFlagImage").style.top = (chartPosition.top + titlePosition.top) + 'px'
//                 document.getElementById("countryFlagImage").hidden = "false";
//            });
            chart.draw(data, options);
            if(countryName == 'India') {
                var settings = {
                            "async": true,
                            "crossDomain": true,
                            "url": "https://corona-virus-world-and-india-data.p.rapidapi.com/api_india",
                            "method": "GET",
                            "headers": {
                                "x-rapidapi-host": "corona-virus-world-and-india-data.p.rapidapi.com",
                                "x-rapidapi-key": "1dbdf75773msh5d8ff64d5df24e3p1783d1jsn46f186225dfe"
                            }
                        }

                $.ajax(settings).done(function (response) {
                    console.log(response);
                    var stateDetails = "";
                    for (var state in response.state_wise) {
                        if(response.state_wise[state].state === undefined) continue;
                        stateDetails += "<tr>"
                        stateDetails +=    "<th>"+response.state_wise[state].state+"</th>"
//                            stateDetails +=    "<td>"+response.state_wise[state].active+"</td>"
                        stateDetails +=    "<td>"+response.state_wise[state].confirmed+"</td>"
                        stateDetails +=    "<td class='text-danger'>"+response.state_wise[state].deaths+"</td>"
                        stateDetails +=    "<td class='text-success'>"+response.state_wise[state].recovered+"</td>"
                        stateDetails += "</tr>"

                    }

                    $('#stateDataId').html(stateDetails);

                });
            } else {
                var settings = {
                            "async": true,
                            "crossDomain": true,
                            "url": "https://covid-19-coronavirus-statistics.p.rapidapi.com/v1/stats",
                            "method": "GET",
                            "headers": {
                                "x-rapidapi-host": "covid-19-coronavirus-statistics.p.rapidapi.com",
                                "x-rapidapi-key": "1dbdf75773msh5d8ff64d5df24e3p1783d1jsn46f186225dfe"
                            }
                        }
                settings['url'] = settings['url']+"?country="+countryName;
                $.ajax(settings).done(function (response) {
                    console.log(response.data);
                    var data = response.data.covid19Stats;
                    var sortedData = data.sort(function compare(a, b) {
                                    var bandA = parseInt(a.confirmed);
                                    var bandB = parseInt(b.confirmed);
                                    var comparison = 0;
                                    if (bandA > bandB) {
                                      comparison = -1;
                                    } else if (bandA < bandB) {
                                      comparison = 1;
                                    }
                                    return comparison;
                                }).filter(function(o) {
                                    return o.confirmed != '0';
                                });
                    var stateDetails = "";
                    for (var state in sortedData) {
                            stateDetails += "<tr>"
                            if(data[state].city == '' && data[state].province == '')
                                stateDetails +="<th>"+ data[state].country+ "</th>"
                            else if(data[state].city == '')
                                stateDetails +="<th>"+data[state].province + "</th>"
                            else
                                stateDetails +="<th>"+data[state].city+ ", " + data[state].province + "</th>"
//                            stateDetails +=    "<td>"+data.confirmed+"</td>"
                            stateDetails +=    "<td>"+data[state].confirmed+"</td>"
                            stateDetails +=    "<td class='text-danger'>"+data[state].deaths+"</td>"
                            stateDetails +=    "<td class='text-success'>"+data[state].recovered+"</td>"
                            stateDetails += "</tr>"

                    }
                    $('#stateDataId').html(stateDetails);

                });

            }
          }


        function goBackToHome(event) {
            window.location.replace("/");
        }

        function displayCountryName(countryName) {
            for(var v in country) {
                if(v == countryName) {
                        return country[countryName];
                }
            }
        }

        function searchFunction() {
          // Declare variables
          var input, filter, table, tr, td, i, txtValue;
          input = document.getElementById("myInputDetail");
          filter = input.value.toUpperCase();
          table = document.getElementById("temp-table");
          tr = table.getElementsByTagName("tr");

          // Loop through all table rows, and hide those who don't match the search query
          for (i = 0; i < tr.length; i++) {
            td = tr[i].getElementsByTagName("td")[0];
//            td1 = tr[i].getElementsByTagName("td")[1];
            if (td) {
              txtValue = td.textContent || td.innerText;
//              txtValue1 = td1.textContent || td1.innerText;
              if (txtValue.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
              } else {
                tr[i].style.display = "none";
              }
            }
          }




        }