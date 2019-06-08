standev <- function(data) {
	return(sqrt(sum((data-(sum(data)/length(data)))^2)/length(data)))
}

morning <- c(39, 35, 39, 39, 40, 37, 41, 39, 42, 40, 37, 35, 38, 36, 40, 35, 38, 36, 39, 35, 38, 35, 39, 38, 41, 39, 38, 40, 38, 41, 41, 37, 34, 41, 37, 41, 35, 39, 36, 41)

standev(morning)
sd(morning)
