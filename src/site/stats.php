<?php 

/**
 * Database Connection Information
 * Change these...
 */
$host = "localhost:3306";
$user = "root";
$password = "";
$database = "minecraftserver";

mysql_connect($host, $user, $password);
@mysql_select_db($database) or die("Unable to select database");

$dir = "./";
  
/*function get_hourlycheck () {
    include DRUPAL_ROOT . "/sites/default/files/db.inc";
    $result = mysql_query ('SELECT * FROM globals');
    mysql_close();
    return mysql_result ($result, 0, 'hourlycheck');
  }*/
  //CREATE OR REPLACE VIEW `total_kills` as select `zf_kills`.`killer_id` AS `killer_id`,count(`zf_kills`.`killer_id`) AS `kill_count` from `zf_kills` group by `zf_kills`.`killer_id` order by count(`zf_kills`.`killer_id`) desc limit 10
  //SELECT `killer_id`, COUNT(`killer_id`) FROM `zf_kills` GROUP BY `killer_id` ORDER BY Count(`killer_id`) DESC LIMIT 10
  //SELECT `zf_players`.`player_name`,`total_kills`.`kill_count` FROM `zf_players` JOIN `total_kills` ON `zf_players`.`id`=`total_kills`.`killer_id` ORDER BY `total_kills`.`kill_count` DESC

echo "
<table border=0 id='leaderboards' cellpadding=10>
  <tr>
    <td>
      <table border=0 id='totalKills'>
        <thead>
          <th>Player</th>
          <th>Kills</th>
        </thead>";

echo "
      </table>
    </td>
  </tr>
</table>
  <tr>
    <th>Item</th>
    <th>Stack Size</th>
    <th>Stock</th>
    <th>Stock Change</th>
    <th>Buy Price</th>
    <th>Buy Change</th>
    <th>Sell Price</th>
    <th>Sell Change</th>
    <th style='display:none;width:0;'>Stock Ratio</th>
  </tr></thead><tbody>
";
  
  $size = count($olddata);
  $i = 1;
  while ($i < $size ) {
    echo "<tr>
            <td><a href='#' onMouseover=\"ddrivetip('<table border = 0><tr><td>Volatlity</td><td>Max Stock</td><td>Max Buy</td><td>Max Sell</td><td>Baseline Buy</td><td>Baseline Sell</td><td>Min Buy</td><td>Min Sell</td></tr><tr><td>{$newdata[$i]['volatility']['value']}</td><td>{$newdata[$i]['stockceil']['value']}</td><td>{$newdata[$i]['maxbuy']['value']}</td><td>{$newdata[$i]['maxsell']['value']}</td><td>{$newdata[$i]['avgbuy']['value']}</td><td>{$newdata[$i]['avgsell']['value']}</td><td>{$newdata[$i]['minbuy']['value']}</td><td>{$newdata[$i]['minsell']['value']}</td></tr></table>', 'white', 600)\"; onMouseout=\"hideddrivetip()\">{$newdata[$i]['name']['value']}</a></td>
            <td><font color='black'>{$newdata[$i]['count']['value']}</font></td>
            <td><font color='{$newdata[$i]['stock']['color']}'>{$newdata[$i]['stock']['value']}</font></td>
            <td><font color='{$newdata[$i]['stock']['change']['color']}'>{$newdata[$i]['stock']['change']['value']}</font></td>
            <td><font color='{$newdata[$i]['buy']['color']}'>{$newdata[$i]['buy']['value']}</font></td>
            <td><font color='{$newdata[$i]['buy']['change']['color']}'>{$newdata[$i]['buy']['change']['value']}</font></td>
            <td><font color='{$newdata[$i]['sell']['color']}'>{$newdata[$i]['sell']['value']}</font></td>
            <td><font color='{$newdata[$i]['sell']['change']['color']}'>{$newdata[$i]['sell']['change']['value']}</font></td>
            <td style='display:none;width:0;'>{$newdata[$i]['stockratio']['value']}</td>
          </tr>";
    $i++;
  }
  
  echo "</tbody></table>";
?>