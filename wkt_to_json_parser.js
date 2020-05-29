var fs = require("fs");

var parse = require("wellknown");

var file_path_to_write="src\\main\\resources\\launuts_geojson_and_shape_files\\node_response.json";

fs.writeFile(file_path_to_write, JSON.stringify(parse(process.argv.slice(2)[0])), function (err,data) {
	if (err) {
	    return console.log(err);
	  }
	  console.log(data);
});
