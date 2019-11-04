package caching;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Title implements Serializable {
    private String tconst;
    private String title;
    private String created;
    private int position;
    private String modified;
    private String description;
    private String url;
    private String titleType;
    private float imdbRating;
    private int runtime;
    private String year;
    private List<String> genres;
    private int numVotes;
    private String releaseDate;
    private List<String> directors;
}
