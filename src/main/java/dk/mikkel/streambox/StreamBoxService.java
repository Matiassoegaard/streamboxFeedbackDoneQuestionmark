package dk.mikkel.streambox;

import java.util.*;
import java.util.stream.Collectors;

public class StreamBoxService {

    private List<Content> catalog = new ArrayList<>();

    public Content addContent(String title, Genre genre, int lengthMinutes, int ageRating) {
        // bevidst “forkert” og uden validering: så tests fejler
        Content newContent = new Content(title,genre,lengthMinutes,ageRating);
        validate(newContent);
        catalog.add(newContent);
        return newContent;
    }

    public void validate(Content content) {
        if(content == null){
            throw new IllegalArgumentException("Content is null");
        }
        if(content.getGenre() == null){
            throw new IllegalArgumentException("Genre is null");
        }
        if(content.getLengthMinutes() <= 0 || content.getLengthMinutes() > 600){
            throw new IllegalArgumentException("Length minutes can not be lower than 0");
        }

        String title  = content.getTitle() == null ? "" : content.getTitle().trim();
        if(title.isEmpty()){
            throw new IllegalArgumentException("Title is empty");
        }

        List<Integer> validAgeRatings = List.of(0,7,11,15,18);
        if(!validAgeRatings.contains(content.getAgeRating())){
            throw new IllegalArgumentException("Age rating is not valid");
        }

    }

    public List<Content> getCatalog() {
        return catalog;
    }

    public Optional<Content> findById(int id) {
        // bevidst: finder aldrig noget
        for (Content content : catalog) {
            if (content.getId() == id) {
                return Optional.of(content);
            }
        }
        return Optional.empty();
    }

    public boolean play(int contentId, int userAge) {
        // bevidst: spiller aldrig noget
        Optional<Content> content = findById(contentId);
        if(content.isPresent() && content.get().getAgeRating() <= userAge) {
            content.get().incrementViews();
            return true;
        }
        if(userAge < 0){
            throw new IllegalArgumentException("User age is negative");
        }
        return false;
    }

    public List<Content> findByGenre(Genre genre) {
        // bevidst: returnerer altid tom
        ArrayList<Content> contentGenres = new ArrayList<>();
        for (Content content : catalog) {
            if (content.getGenre() == genre) {
                contentGenres.add(content);
            }
        }
        contentGenres.sort((a,b) -> a.getTitle().compareTo(b.getTitle()));
        return contentGenres;
    }

    public int totalRuntimeByGenre(Genre genre) {
        // bevidst: 0
        ArrayList<Content> contentGenres = new ArrayList<>();
        int totalRuntime = 0;
        for (Content content : catalog) {
            if (content.getGenre() == genre) {
                contentGenres.add(content);
                totalRuntime += content.getLengthMinutes();
            }
        }
        return totalRuntime;
    }

    public List<Content> topTrending(int n) {
        // bevidst: tom liste (og ingen validering)
       if(n <= 0){
           throw new IllegalArgumentException("Number of trending is less than 0");
       }
       List<Content> result = new ArrayList<>();
       for (Content content : catalog) {
           result.add(content);
       }
        result.sort(Comparator.comparingInt(Content::getViews).reversed()
                .thenComparing(Content::getTitle));
        return result.subList(0, Math.min(n, result.size()));
    }

    public Optional<Content> mostViewedInGenre(Genre genre) {
        // bevidst: ingen resultater
        List<Content> result = new ArrayList<>();
        for (Content content : catalog) {
            if (content.getGenre() == genre) {
                result.add(content);
            }
        }
        if(result.isEmpty()){
            return Optional.empty();
        }
        result.sort(Comparator.comparingInt(Content::getViews).reversed()
                .thenComparing(Content::getTitle));
        return Optional.of(result.getFirst());
    }

    public boolean removeById(int id) {
        // bevidst: fjerner aldrig noget
        for (Content content : catalog) {
            if (content.getId() == id) {
                catalog.remove(content);
                if (findById(id).isEmpty()) {
                    return true;
                }
            }

        }
        if (!catalog.contains(findById(id))) {
            return false;
        }
        return true;
    }

}
