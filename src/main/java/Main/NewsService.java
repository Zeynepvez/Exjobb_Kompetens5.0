package Main;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public News saveNews(News news) {
        return newsRepository.save(news);
    }

    public List<News> findAllNews() {
        return newsRepository.findAll();
    }

    public News findById(Long id) {
        return newsRepository.findById(id).orElse(null);
    }

    public void deleteNewsById(Long id) {
        newsRepository.deleteById(id);
    }
}