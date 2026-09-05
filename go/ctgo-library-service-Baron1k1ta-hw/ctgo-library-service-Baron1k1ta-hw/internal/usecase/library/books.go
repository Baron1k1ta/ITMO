package library

import (
	"context"

	"github.com/google/uuid"

	"github.com/project/library/internal/entity"
)

func (l *libraryImpl) RegisterBook(ctx context.Context, name string, authorID []string) (entity.Book, error) {
	return l.booksRepository.CreateBook(ctx, entity.Book{
		ID:       uuid.New().String(),
		Name:     name,
		AuthorID: authorID,
	})
}

func (l *libraryImpl) GetBook(ctx context.Context, bookID string) (entity.Book, error) {
	return l.booksRepository.GetBook(ctx, bookID)
}

func (l *libraryImpl) UpdateBook(ctx context.Context, bookID string, name string, authorID []string) error {
	existingBook, err := l.booksRepository.GetBook(ctx, bookID)

	if err != nil {
		return entity.ErrBookNotFound
	}

	existingBook.Name = name
	existingBook.AuthorID = authorID

	return l.booksRepository.UpdateBook(ctx, existingBook)

}

func (l *libraryImpl) GetAuthorBooks(ctx context.Context, authorID string) ([]entity.Book, error) {
	return l.booksRepository.GetAuthorBooks(ctx, authorID)
}
