          var stateDetails = "";
          var reducedData = {};
          var localCountryName = countryName;
          google.charts.load('current', {'packages':['corechart']});
          google.charts.setOnLoadCallback(drawChart);

          function drawChart() {
//            $("#curve_chart").after('<div class="d-flex justify-content-center"><div class="spinner-grow text-primary" role="status"><span class="sr-only">Loading...</span></div></div>');
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
             this.countryName = countryName;
             var data = google.visualization.arrayToDataTable(chartData);
             var options = {
             title: 'Trend of Daily Cases',
//             titlePosition: in,
             curveType: 'function',
             vAxis: {
                     viewWindow: {
                         min:0
                     }
                 },
             legend: { position: 'bottom' },
             explorer: {
                 actions: ['dragToZoom', 'rightClickToReset'],
                 axis: 'horizontal',
                 keepInBounds: true,
                 maxZoomIn: 4.0
             }
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


            setTimeout(function() {
                  console.log('callback');
                  chart.draw(data, options);
//                  $('#loading').remove();
              }, 0);

          }


        function goBackToHome(event) {
//            window.location.replace("/");
            history.back()
        }

        function displayCountryName(countryName) {
            for(var v in country) {
                if(v == countryName) {
                        return country[countryName];
                }
            }
            return countryName;
        }

        $(document).ready(function(){
          $("#myInput").on("keyup", function() {
            var value = $(this).val().toLowerCase();
            $("#stateTableBodyId tr").filter(function() {
              $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
            });
          });
        });

        $(document).ready(function(){
            if(!$.active) {
                if(localCountryName == 'India') {
                    var settings = {
                                "async": true,
                                "crossDomain": true,
                                "url": "https://corona-virus-world-and-india-data.p.rapidapi.com/api_india",
                                "method": "GET",
                                "headers": {
                                    "x-rapidapi-host": rapidapi_host_IN,
                                    "x-rapidapi-key": rapidapi_key
                                }
                            }

                    $.ajax(settings).done(function (response) {
                        $("#curve_chart").after('<div class="d-flex align-items-center text-secondary" id="loading"><strong>Loading...</strong><div class="spinner-border spinner-border-sm text-secondary ml-auto" role="status" aria-hidden="true"></div></div>');
//                        console.log(response);
                        stateDetails = "";
                        for (var state in response.state_wise) {
                            if(response.state_wise[state].state === undefined) continue;
                            stateDetails += "<tr>"
                            stateDetails +=    "<th>"+response.state_wise[state].state+"</th>"
    //                            stateDetails +=    "<td>"+response.state_wise[state].active+"</td>"
                            stateDetails +=    "<td style='text-align:right;'>"+formatNumber(response.state_wise[state].confirmed)+"</td>"
                            stateDetails +=    "<td class='text-danger' style='text-align:right;'>"+formatNumber(response.state_wise[state].deaths)+"</td>"
                            stateDetails +=    "<td class='text-success' style='text-align:right;'>"+formatNumber(response.state_wise[state].recovered)+"</td>"
                            stateDetails += "</tr>"
                        }
                          setTimeout(function() {
                              console.log('callback');
                              $('#stateTableBodyId').html(stateDetails);
                              $('#loading').remove();
                          }, 1000);
//                        $('#stateTableBodyId').html(stateDetails);

                    });
                } else {

                    var settings = {
                                "async": true,
                                "crossDomain": true,
                                "url": "https://covid-19-coronavirus-statistics.p.rapidapi.com/v1/stats",
                                "method": "GET",
                                "headers": {
                                    "x-rapidapi-host": rapidapi_host_US,
                                    "x-rapidapi-key": rapidapi_key
                                }
                            }
                    settings['url'] = settings['url']+"?country="+localCountryName;
                    $.ajax(settings).done(function (response) {
//                        console.log(response.data);
                        $("#curve_chart").after('<div class="d-flex align-items-center text-secondary" id="loading"><strong>Loading...</strong><div class="spinner-border spinner-border-sm text-secondary ml-auto" role="status" aria-hidden="true"></div></div>');
                        stateDetails = "";
                        var data = response.data.covid19Stats;
                        /*Test Code*/
                        let resp_data = response.data.covid19Stats;
                        let property = 'province';
                        reducedData = resp_data.reduce(function(accumulator, currentObj) {
                            let key = '';
                            if(currentObj.city == '' && currentObj.province == '')
                                key = currentObj.country;
                            else
                                key = currentObj.province;

                            if(!accumulator[key])
                                accumulator[key] = {region: key, confirmed: 0, deaths: 0, recovered: 0, subRegions: []};

                            accumulator[key].confirmed += currentObj.confirmed;
                            accumulator[key].deaths += currentObj.deaths;
                            accumulator[key].recovered += currentObj.recovered;
                            accumulator[key].subRegions.push(currentObj);
                            return accumulator;
                          }, {});

                        var dataArraySorted = Object.values(reducedData).sort(function compare(a, b) {
                                             var A = parseInt(a.confirmed);
                                             var B = parseInt(b.confirmed);
                                             var comparison = 0;
                                             if (A > B) {
                                               comparison = -1;
                                             } else if (A < B) {
                                               comparison = 1;
                                             }
                                             return comparison;
                                         }).forEach(function(state) {
                                               stateDetails += "<tr>";
                                               if(state.subRegions.length > 1)
                                                stateDetails +="<td><a href='#' class='open-modal'>"+state.region + "</a></td>";
                                               else
                                                stateDetails +="<td>"+state.region + "</td>";
                                               stateDetails +=    "<td  style='text-align:right;'>"+formatNumber(state.confirmed)+"</td>";
                                               stateDetails +=    "<td style='text-align:right;' class='text-danger'>"+formatNumber(state.deaths)+"</td>";
                                               stateDetails +=    "<td style='text-align:right;' class='text-success'>"+formatNumber(state.recovered)+"</td>";
                                               stateDetails += "</tr>";
                                       });
//                        console.log(dataArraySorted);
//                        var sortedData = data.sort(function compare(a, b) {
//                                        var A = parseInt(a.confirmed);
//                                        var B = parseInt(b.confirmed);
//                                        var comparison = 0;
//                                        if (A > B) {
//                                          comparison = -1;
//                                        } else if (A < B) {
//                                          comparison = 1;
//                                        }
//                                        return comparison;
//                                    }).filter(function(o) {
//                                        return o.confirmed != '0';
//                                    }).forEach(function(state) {
//                                            stateDetails += "<tr>";
//                                            if(state.city == '' && state.province == '')
//                                                stateDetails +="<th>"+ state.country+ "</th>";
//                                            else if(state.city == '')
//                                            stateDetails +="<th>"+state.province + "</th>";
//                                            else
//                                                stateDetails +="<th>"+state.city+ ", " + state.province + "</th>";
//                //                            stateDetails +=    "<td>"+data.confirmed+"</td>"
//                                            stateDetails +=    "<td>"+state.confirmed+"</td>";
//                                            stateDetails +=    "<td class='text-danger'>"+state.deaths+"</td>";
//                                            stateDetails +=    "<td class='text-success'>"+state.recovered+"</td>";
//                                            stateDetails += "</tr>";
//                                    });
                                      setTimeout(function() {
                                          console.log('callback');
                                          $('#stateTableBodyId').html(stateDetails);
                                          $('#loading').remove();
                                      }, 1000);

                    });

                }
             }
        });

        $(document).on("click", ".open-modal",
            function (event) {
                    var subRegionParam = event.currentTarget.innerText;
                    $("#subRegionModalLongTitle").html(subRegionParam);
                    stateDetails = "";
                    reducedData[subRegionParam].subRegions.sort(function compare(a, b) {
                         var A = parseInt(a.confirmed);
                         var B = parseInt(b.confirmed);
                         var comparison = 0;
                         if (A > B) {
                           comparison = -1;
                         } else if (A < B) {
                           comparison = 1;
                         }
                         return comparison;

                    }).forEach(function(item) {
//                        console.log(item);
                         stateDetails += "<tr>";
                         stateDetails +="<th>"+item.city + "</th>";
                         stateDetails +=    "<td style='text-align:right;'>"+formatNumber(item.confirmed)+"</td>";
                         stateDetails +=    "<td style='text-align:right;' class='text-danger'>"+formatNumber(item.deaths)+"</td>";
                         stateDetails +=    "<td style='text-align:right;' class='text-success'>"+formatNumber(item.recovered)+"</td>";
                         stateDetails += "</tr>";
                    });
                    $('#subRegion-temp-table').html(stateDetails);
                    $('#exampleModalCenter').modal('show');
            });

        function formatNumber(num) {
          return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,')
        }