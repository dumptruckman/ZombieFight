<?php 
/**
 * Database Connection Information
 * Change these...
 */
$host = "localhost:3306";
$user = "root";
$password = "";
$database = "minecraftserver";

function mysql_fetch_all($res) {
   while($row=mysql_fetch_array($res)) {
       $return[] = $row;
   }
   return $return;
}

function create_table_row($dataArr) {
    echo "<tr>";
    for($j = 0; $j < count($dataArr); $j++) {
        echo "<td>".$dataArr[$j]."</td>";
    }
    echo "</tr>";
}

function get_total_kills($type) {
  return mysql_query("
      SELECT
      `zf_players`.`player_name`,
      `grand_total_kills`.`kill_count`
      FROM `zf_players`
      JOIN `grand_total_kills`
      ON `zf_players`.`id`=`grand_total_kills`.`killer_id`
      WHERE `grand_total_kills`.`victim_type`=
      (
        SELECT `id`
        FROM `zf_player_type`
        WHERE `type_name`='{$type}'
      )
      ORDER BY `grand_total_kills`.`kill_count` DESC
      LIMIT 10
      ");
}

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
      <table border=1 id='humanKills' cellpadding=3>
        <thead>
          <th>Player Name</th>
          <th>Human Kills</th>
        </thead>";
$result = get_total_kills("HUMAN");
$all = mysql_fetch_all($result);
for($i = 0; $i < count($all); $i++) {
    create_table_row($all[$i]);
}
echo "
      </table>
    </td>
    <td>
      <table border=1 id='zombieKills' cellpadding=3>
        <thead>
          <th>Player Name</th>
          <th>Zombie Kills</th>
        </thead>";
$result = get_total_kills("ZOMBIE");
$all = mysql_fetch_all($result);
for($i = 0; $i < count($all); $i++) {
    create_table_row($all[$i]);
}
echo "
      </table>
    </td>
  </tr>
</table>
";
mysql_close();
?>