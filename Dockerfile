FROM node:alpine

# Create work directory
WORKDIR /usr/app

# Install runtime dependencies
RUN npm install yarn -g

# Copy app source to work directory
COPY . /usr/app

# Install app dependencies
RUN yarn install

# Build app
RUN npm build

# Run app
CMD ["npm", "start"]
