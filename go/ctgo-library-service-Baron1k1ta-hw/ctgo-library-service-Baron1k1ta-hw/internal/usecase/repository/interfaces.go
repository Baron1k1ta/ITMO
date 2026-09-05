package repository

import (
	"context"

	"github.com/project/library/internal/entity"
)

type (
	AuthorRepository interface {
		CreateAuthor(ctx context.Context, author entity.Author) (entity.Author, error)
		GetAuthor(ctx context.Context, authorID string) (entity.Author, error)
		UpdateAuthor(ctx context.Context, author entity.Author) error
	}

	BooksRepository interface {
		CreateBook(ctx context.Context, book entity.Book) (entity.Book, error)
		GetBook(ctx context.Context, bookID string) (entity.Book, error)
		UpdateBook(ctx context.Context, book entity.Book) error
		GetAuthorBooks(ctx context.Context, authorID string) ([]entity.Book, error)
	}
)
