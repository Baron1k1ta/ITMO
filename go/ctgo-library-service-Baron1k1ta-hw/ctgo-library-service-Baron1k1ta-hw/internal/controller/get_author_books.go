package controller

import (
	"github.com/project/library/generated/api/library"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

func (i *implementation) GetAuthorBooks(req *library.GetAuthorBooksRequest, stream library.Library_GetAuthorBooksServer) error {
	if err := req.ValidateAll(); err != nil {
		return status.Error(codes.InvalidArgument, err.Error())
	}

	books, err := i.booksUseCase.GetAuthorBooks(stream.Context(), req.GetAuthorId())
	if err != nil {
		return i.convertErr(err)
	}

	for _, book := range books {
		if err := stream.Send(&library.Book{
			Id:       book.ID,
			Name:     book.Name,
			AuthorId: book.AuthorID,
		}); err != nil {
			return err
		}
	}

	return nil
}
