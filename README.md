[![Codacy Badge](https://app.codacy.com/project/badge/Grade/15ae3d9904fb4323b682ae48f96275d3)](https://www.codacy.com/gh/pg-dp/LauNuts?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=pg-dp/LauNuts&amp;utm_campaign=Badge_Grade)

# LauNuts

Download the LauNuts knowledge graph at the [Hobbit server](https://hobbitdata.informatik.uni-leipzig.de/OPAL/).

## Statistics

```
Countries:       1 (Germany)
NUTS-1:         16 (Federal states)
NUTS-2:         38 (Government regions)
NUTS-3:        401 (Districts)
LAU:        11,087 (Municipalities)
Total:      11,543

GeoData:    10,695
No GeoData:    848

Triples:   100,360
```

## LauNuts generator

This code creates a knowledge graph for German regions and cities consisting of

- [Local Administrative Units (LAU)](https://ec.europa.eu/eurostat/web/nuts/local-administrative-units)
- [Nomenclature of Territorial Units for Statistics (NUTS)](https://ec.europa.eu/eurostat/web/nuts/background)
- GeoData from [DBpedia](http://dbpedia.org/)


## Usage of the data generator

* Download the required data (backup available at the [Hobbit server](https://hobbitdata.informatik.uni-leipzig.de/OPAL/))
    * nuts.rdf at [data.europa.eu](http://data.europa.eu/euodp/repository/ec/estat/nuts/nuts.rdf)
    * EU-28-LAU-2019-NUTS-2016-DE.csv included in [XLSX at ec.europa.eu](https://ec.europa.eu/eurostat/documents/345175/501971/EU-28-LAU-2019-NUTS-2016.xlsx)
* Execute the main method in org.dice_research.opal.launuts.Main
* Edit the created configuration file 'launuts-configuration.xml'
* Execute the main method in org.dice_research.opal.launuts.Main again

## Note

This is not related to the musical instrument [launut](https://www.metmuseum.org/art/collection/search/501966).

## Credits

[Data Science Group (DICE)](https://dice-research.org/) at [Paderborn University](https://www.uni-paderborn.de/)

Contact: Adrian Wilke

This work has been supported by the German Federal Ministry of Transport and Digital Infrastructure (BMVI) in the project [Open Data Portal Germany (OPAL)](http://projekt-opal.de/) (funding code 19F2028A).
