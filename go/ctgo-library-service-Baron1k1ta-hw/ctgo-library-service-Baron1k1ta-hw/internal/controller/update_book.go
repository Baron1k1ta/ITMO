package controller

import (
	"context"

	"github.com/project/library/generated/api/library"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

func (i *implementation) UpdateBook(ctx context.Context, req *library.UpdateBookRequest) (*library.UpdateBookResponse, error) {
	if err := req.ValidateAll(); err != nil {
		return nil, status.Error(codes.InvalidArgument, err.Error())
	}

	for _, authorID := range req.GetAuthorIds() {
		_, err := i.authorUseCase.GetAuthor(ctx, authorID)
		if err != nil {
			return nil, status.Error(codes.NotFound, "author not found: "+authorID)
		}
	}

	err := i.booksUseCase.UpdateBook(ctx, req.GetId(), req.GetName(), req.GetAuthorIds())
	if err != nil {
		return nil, i.convertErr(err)
	}

	return &library.UpdateBookResponse{}, nil
}
