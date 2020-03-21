        function searchFunction() {
          // Declare variables
          var input, filter, table, tr, td, i, txtValue;
          input = document.getElementById("myInput");
          filter = input.value.toUpperCase();
          table = document.getElementById("myTable");
          tr = table.getElementsByTagName("tr");

          // Loop through all table rows, and hide those who don't match the search query
          for (i = 0; i < tr.length; i++) {
            td = tr[i].getElementsByTagName("td")[0];
            td1 = tr[i].getElementsByTagName("td")[1];
            if (td || td1) {
              txtValue = td.textContent || td.innerText;
              txtValue1 = td1.textContent || td1.innerText;
              if (txtValue.toUpperCase().indexOf(filter) > -1 || txtValue1.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
              } else {
                tr[i].style.display = "none";
              }
            }
          }
        }

        function sortTable(n) {
          var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0, v1, v2;
          table = document.getElementById("myTable");
          switching = true;
          // Set the sorting direction to ascending:
          dir = "asc";
          /* Make a loop that will continue until
          no switching has been done: */
          while (switching) {
            // Start by saying: no switching is done:
            switching = false;
            rows = table.rows;
            /* Loop through all table rows (except the
            first, which contains table headers): */
            for (i = 1; i < (rows.length - 1); i++) {
              // Start by saying there should be no switching:
              shouldSwitch = false;
              /* Get the two elements you want to compare,
              one from current row and one from the next: */
              x = rows[i].getElementsByTagName("TD")[n];
              y = rows[i + 1].getElementsByTagName("TD")[n];
              /* Check if the two rows should switch place,
              based on the direction, asc or desc: */


              if (isNaN(x.innerHTML) || isNaN(y.innerHTML)) {
                v1 = x.innerHTML.toLowerCase();
                v2= y.innerHTML.toLowerCase();
              } else {
                v1 = parseInt(x.innerHTML);
                v2= parseInt(y.innerHTML);
              }

              if (dir == "asc") {
                //if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                if (v1 > v2) {
                  // If so, mark as a switch and break the loop:
                  shouldSwitch = true;
                  break;
                }
              } else if (dir == "desc") {
                //if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
                if (v1 < v2) {
                  // If so, mark as a switch and break the loop:
                  shouldSwitch = true;
                  break;
                }
              }
            }
            if (shouldSwitch) {
              /* If a switch has been marked, make the switch
              and mark that a switch has been done: */
              rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
              switching = true;
              // Each time a switch is done, increase this count by 1:
              switchcount ++;
            } else {
              /* If no switching has been done AND the direction is "asc",
              set the direction to "desc" and run the while loop again. */
              if (switchcount == 0 && dir == "asc") {
                dir = "desc";
                switching = true;
              }
            }
          }
        }

        function cellStyle(value, row, index) {
            return {
                classes: value.trim() == 0 ? 'yes' : 'no'
            };
        }

        function goToDetails(event) {
            //alert(event.currentTarget.innerText);
            var str = event.currentTarget.innerText;
            var arr = str.split("/\s+/");
            for (i = 0; i < arr.length; i++) {
                console.log(arr[i]);
            }

            window.location.replace("/?id=" + arr[0]);
        }

/*
        // Load google charts
        google.charts.load('current', {'packages':['corechart']});
        google.charts.setOnLoadCallback(drawChart);

        // Draw the chart and set the chart values
        function drawChart() {
          var chartData = [];
          chartData.push(['Country','Confirmed Cases']);
          table = document.getElementById("myTable");
          tr = table.getElementsByTagName("tr");
          var others = 0;

          // Loop through all table rows,
          for (i = 0; i < tr.length; i++) {
            td = tr[i].getElementsByTagName("td")[0];
            td1 = tr[i].getElementsByTagName("td")[1];
            if (td || td1) {
              var txtValue = td.textContent || td.innerText;
              var txtValue1 = td1.textContent || td1.innerText;
              if(parseInt(txtValue1) > 1000)
                chartData.push([txtValue,parseInt(txtValue1)]);
              else
                 others += parseInt(txtValue1);
            }

          }
          chartData.push(['Others', others]);
          var data = google.visualization.arrayToDataTable(chartData);

          // Optional; add a title and set the width and height of the chart
          var options = {'title':'Confirmed Cases', 'width':550, 'height':400};

          // Display the chart inside the <div> element with id="piechart"
          var chart = new google.visualization.PieChart(document.getElementById('piechart'));
          chart.draw(data, options);
        }

*/