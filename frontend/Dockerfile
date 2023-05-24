FROM node:alpine

RUN addgroup \
    --gid 1001 \
    --system \
    executor \
    && adduser \
    --disabled-password \
    --home /app \
    --ingroup executor \
    --system \
    --uid 1001 \
    executor
USER executor

WORKDIR /app

COPY package*.json ./

RUN npm ci

COPY . .

RUN npm run build

CMD ["npm", "start"]