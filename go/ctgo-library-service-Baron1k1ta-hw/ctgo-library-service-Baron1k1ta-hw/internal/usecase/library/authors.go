package library

import (
	"context"

	"github.com/google/uuid"
	"github.com/project/library/internal/entity"
)

func (l *libraryImpl) RegisterAuthor(ctx context.Context, authorName string) (entity.Author, error) {
	return l.authorRepository.CreateAuthor(ctx, entity.Author{
		ID:   uuid.New().String(),
		Name: authorName,
	})
}

func (l *libraryImpl) GetAuthor(ctx context.Context, authorID string) (entity.Author, error) {
	return l.authorRepository.GetAuthor(ctx, authorID)
}

func (l *libraryImpl) UpdateAuthor(ctx context.Context, authorID string, name string) error {

	existingAuthor, err := l.authorRepository.GetAuthor(ctx, authorID)

	if err != nil {
		return entity.ErrAuthorNotFound
	}

	existingAuthor.Name = name

	return l.authorRepository.UpdateAuthor(ctx, existingAuthor)

}
