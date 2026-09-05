package repository

import (
	"context"
	"sync"

	"github.com/project/library/internal/entity"
)

var _ AuthorRepository = (*inMemoryImpl)(nil)
var _ BooksRepository = (*inMemoryImpl)(nil)

type inMemoryImpl struct {
	authorsMx *sync.RWMutex
	authors   map[string]*entity.Author

	booksMx *sync.RWMutex
	books   map[string]*entity.Book
}

func NewInMemoryRepository() *inMemoryImpl {
	return &inMemoryImpl{
		authorsMx: new(sync.RWMutex),
		authors:   make(map[string]*entity.Author),

		books:   map[string]*entity.Book{},
		booksMx: new(sync.RWMutex),
	}
}

func (i *inMemoryImpl) CreateAuthor(_ context.Context, author entity.Author) (entity.Author, error) {
	i.authorsMx.Lock()
	defer i.authorsMx.Unlock()

	if _, ok := i.authors[author.ID]; ok {
		return entity.Author{}, entity.ErrAuthorAlreadyExists
	}

	i.authors[author.ID] = &author
	return author, nil
}

func (i *inMemoryImpl) CreateBook(_ context.Context, book entity.Book) (entity.Book, error) {
	i.booksMx.Lock()
	defer i.booksMx.Unlock()

	if _, ok := i.books[book.ID]; ok {
		return entity.Book{}, entity.ErrBookAlreadyExists
	}
	i.books[book.ID] = &book
	return book, nil
}

func (i *inMemoryImpl) GetBook(_ context.Context, bookID string) (entity.Book, error) {
	i.booksMx.RLock()
	defer i.booksMx.RUnlock()

	if v, ok := i.books[bookID]; !ok {
		return entity.Book{}, entity.ErrBookNotFound
	} else {
		return *v, nil
	}
}

func (i *inMemoryImpl) UpdateBook(_ context.Context, book entity.Book) error {
	i.booksMx.Lock()
	defer i.booksMx.Unlock()

	if _, ok := i.books[book.ID]; !ok {
		return entity.ErrBookNotFound
	}

	i.books[book.ID] = &book
	return nil
}
func (i *inMemoryImpl) GetAuthor(_ context.Context, authorID string) (entity.Author, error) {
	i.authorsMx.RLock()
	defer i.authorsMx.RUnlock()

	if v, ok := i.authors[authorID]; !ok {
		return entity.Author{}, entity.ErrAuthorNotFound
	} else {
		return *v, nil
	}
}
func (i *inMemoryImpl) UpdateAuthor(_ context.Context, author entity.Author) error {
	i.authorsMx.Lock()
	defer i.authorsMx.Unlock()

	if _, ok := i.authors[author.ID]; !ok {
		return entity.ErrAuthorNotFound
	}

	i.authors[author.ID] = &author
	return nil
}
func (i *inMemoryImpl) GetAuthorBooks(_ context.Context, authorID string) ([]entity.Book, error) {
	i.booksMx.RLock()
	defer i.booksMx.RUnlock()

	var books []entity.Book
	for _, book := range i.books {
		for _, aID := range book.AuthorID {
			if aID == authorID {
				books = append(books, *book)
				break
			}
		}
	}

	return books, nil
}
