var serverAddress = 'http://localhost:8090';
var endpointList = [
    'http://linked.opendata.cz/sparql',
    'http://dbpedia.org/sparql'
];
var predefinedLangs = [
     {value: 'cz', label: 'Czech'},
     {value: 'de', label: 'Deutch'},
     {value: 'en', label: 'English'},
     {value: 'fr', label: 'French'},
     {value: 'es', label: 'Spanish'},
];
var predefinedNamespaces = {
	"dc": "http://purl.org/dc/terms/",
	"rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
	"rdfs": "http://www.w3.org/2000/01/rdf-schema#",
	"skos": "http://www.w3.org/2004/02/skos/core#"
};
var minWorkspaceWidth = 1560;
var minWorkspaceHeight = 600;