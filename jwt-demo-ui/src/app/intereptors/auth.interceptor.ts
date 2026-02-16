import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { AuthService } from "../services/auth.service";
import { catchError, concatMap, throwError } from "rxjs";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const auth = inject(AuthService)

    // Attach access token in to header
    const addToken = (request: typeof req) => 
        auth.token ? request.clone({
            setHeaders: { Authorization: 'Bearer '+ auth.token}
        }) : request

        return next(addToken(req)).pipe(
            catchError((error: HttpErrorResponse) => {
                console.log(req)
                // only attempt refresh for non-refresh api calls
                if(error.status == 401 && !req.url.includes('/auth/refresh')){
                    return auth.refreshToken().pipe(
                        // after refresh completes, retry original request
                        concatMap(()=> next(addToken(req))),
                        // if refresh also fails -> logout
                        catchError(err => {
                            auth.logout().subscribe();
                            return throwError(() => err)
                        })
                    )
                }
                return throwError(()=> error)
            })
        )
}