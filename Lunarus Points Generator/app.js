const config = require("./config.json");
const Discord = require("discord.js");
const bot = new Discord.Client();
const mysql = require("mysql");
bot.commands = new Discord.Collection();
let embed = config.embed;

var con = mysql.createConnection({
  host: "DATABASE IP GOES HERE",
  user: "DATABASE USERNAME GOES HERE",
  password: "YOUR PASSWORD GOES HERE",
  database: "YOUR DATABASE GOES HERE"
});

con.connect(err => {
  if(err) throw err;
  console.log("\nConnected to database!");
});

function generateFeathers(){
  let min = 10;
  let max = 50;

  return Math.floor(Math.random() * (max - min + 1)) + min;
}

bot.on("ready", async() => {
  console.log(`${bot.user.username} is online in ${bot.guilds.cache.size} servers!`);
  bot.user.setActivity("");
});

bot.on("message", async message =>{
  if(message.author.bot) return;
  if(message.channel.type === "dm") return;
  if(message.content.startsWith(config.prefix)) return;

con.query(`SELECT * FROM [insert datavase table here] WHERE userid = ${message.author.id} AND serverid = ${message.guild.id}`, (err, rows) =>{
    if(err) throw err;

    let sql, feathers;

    if(rows.length < 1){
      sql = `INSERT INTO feathers (user, userid, server, serverid, feathers) VALUES ('${message.author.username}', '${message.author.id}', '${message.guild.name}', '${message.guild.id}', ${generateFeathers()})`;
    }else{
      feathers = rows[0].feathers;
      sql = `UPDATE feathers SET feathers = ${feathers + generateFeathers()} WHERE userid = '${message.author.id}'`;
    }
    con.query(sql, console.log);
  });
});

bot.login(config.token)
