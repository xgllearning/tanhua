package com.study.mongo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("places")
@CompoundIndex(name = "location_index", def = "{'location': '2dsphere'}")
public class Places {

    private ObjectId id;

    private String title;

    private String address;

    private GeoJsonPoint location;
}
